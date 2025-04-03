package KChat.Configuration;

import KChat.Common.Constants;
import KChat.DbOption.Service.IUserGroupService;
import KChat.DbOption.ServiceImpl.UserGroupService;
import KChat.Entity.VO.HeartbeatMessage;
import KChat.Model.SentChatMessageModel;
import KChat.NettyServer;
import KChat.Service.RedisCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class NettyChannelConfig {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RedisCache redis;
    @Resource(type = UserGroupService.class)
    private IUserGroupService groupService;

    @Component
    @ChannelHandler.Sharable
    public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame>{
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
            if (frame instanceof TextWebSocketFrame) {
                // 处理文本消息
                String frameContent = ((TextWebSocketFrame) frame).text();
                SentChatMessageModel message = objectMapper.readValue(frameContent,SentChatMessageModel.class);
                TextWebSocketFrame frameToSend = new TextWebSocketFrame(frameContent);
                boolean isGroup = message.getContactId().indexOf(Constants.GroupIdPrefix)>=0;
                if(isGroup&&NettyServer.GroupChannels.containsKey(message.getContactId()))
                    NettyServer.GroupChannels.get(message.getContactId()).writeAndFlush(frameToSend);
                else if(!isGroup&&NettyServer.UserChannels.containsKey(message.getContactId()))
                    NettyServer.UserChannels.get(message.getContactId()).writeAndFlush(frameToSend);
            } else if (frame instanceof CloseWebSocketFrame) {
                // 处理关闭连接
                channelInactive(ctx);
            }
        }

        private void addChannel(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            String token = channel.attr(NettyServer.TokenAttr).get();
            String userId = channel.attr(NettyServer.UserIdAttr).get();
            List<String> groupIds = groupService.getUserGroupIds(userId,redis);
            String key = String.format("%s_token",userId);
            if(!redis.has(key)||!redis.get(key).equals(token))
            {
                channel.closeFuture().sync();
                return;
            }
            for(String groupId:groupIds){
                if(!NettyServer.GroupChannels.containsKey(groupId)){
                    ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                    channelGroup.add(channel);
                    NettyServer.GroupChannels.put(groupId,channelGroup);
                }
                else
                    NettyServer.GroupChannels.get(groupId).add(channel);
            }
            NettyServer.UserChannels.put(userId,channel);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
                var com = (WebSocketServerProtocolHandler.HandshakeComplete)evt;
                String uri = com.requestUri();
                QueryStringDecoder decoder = new QueryStringDecoder(uri);
                var params = decoder.parameters();
                if(params.size()>Constants.None){
                    for (String key:params.keySet()){
                        if(key.equals(Constants.WebSocketUserId))
                            ctx.channel().attr(NettyServer.UserIdAttr).set(params.get(key).get(0));
                        if(key.equals(Constants.JwtTokenSign))
                            ctx.channel().attr(NettyServer.TokenAttr).set(params.get(key).get(0));
                    }
                }
                addChannel(ctx);
            }
            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            String userId = channel.attr(NettyServer.UserIdAttr).get();
            NettyServer.UserChannels.remove(userId);
            List<String> groupIds = groupService.getUserGroupIds(userId,redis);
            for(String groupId:groupIds){
                if(NettyServer.GroupChannels.containsKey(groupId))
                {
                    NettyServer.GroupChannels.get(groupId).remove(channel);
                    if(NettyServer.GroupChannels.get(groupId).size()==Constants.None)
                        NettyServer.GroupChannels.remove(groupId);
                }
            }
            NettyServer.UserChannels.remove(userId);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }

    @Component
    @ChannelHandler.Sharable
    public class HeartbeatFrameHandler extends ChannelInboundHandlerAdapter{
        private final AttributeKey<ScheduledFuture<?>> timeoutAttr = AttributeKey.newInstance("timeout");
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof TextWebSocketFrame)
            {
                try {
                    TextWebSocketFrame frame = (TextWebSocketFrame) msg;
                    ObjectMapper json = new ObjectMapper();
                    HeartbeatMessage heartbeat = json.readValue(frame.text(), HeartbeatMessage.class);
                    heartbeat.setIsResponse(true);
                    TextWebSocketFrame newFrame = new TextWebSocketFrame(json.writeValueAsString(heartbeat));
                    resetHeartbeatTimer(ctx);
                    ctx.channel().writeAndFlush(newFrame);
                }
                catch (Exception ex){
                    super.channelRead(ctx,msg);
                }
            }
            else
              super.channelRead(ctx,msg);
        }

        private void resetHeartbeatTimer(ChannelHandlerContext ctx) {
            // 移除旧的定时器（如果存在）
            ScheduledFuture<?> future = ctx.channel().attr(timeoutAttr).get();
            if (future != null) {
                future.cancel(false);
            }

            // 设置新的定时器
            ScheduledFuture<?> newFuture = ctx.executor().schedule(() -> {
                System.out.println("心跳超时，关闭连接");
                ctx.close();
            }, Constants.HeartbeatTimeout, TimeUnit.SECONDS);

            ctx.channel().attr(timeoutAttr).set(newFuture);
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            resetHeartbeatTimer(ctx);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            ScheduledFuture<?> task = ctx.channel().attr(timeoutAttr).get();
            if(task!=null)
                task.cancel(false);
        }
    }
}
