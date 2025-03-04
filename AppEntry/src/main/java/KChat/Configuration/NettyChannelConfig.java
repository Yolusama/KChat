package KChat.Configuration;

import KChat.Entity.ChatMessage;
import KChat.NettyServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyChannelConfig {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public SimpleChannelInboundHandler<WebSocketFrame> webSocketFrameHandler(){
        return new SimpleChannelInboundHandler<>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
                if (frame instanceof TextWebSocketFrame) {
                    // 处理文本消息
                    String frameContent = ((TextWebSocketFrame) frame).text();
                    ChatMessage message = objectMapper.readValue(frameContent,ChatMessage.class);
                    if(message.isGroup())
                        NettyServer.Channels.writeAndFlush(frame);
                    else
                        ctx.channel().writeAndFlush(frame);
                } else if (frame instanceof CloseWebSocketFrame) {
                    // 处理关闭连接
                    channelInactive(ctx);
                }
            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                Channel channel = ctx.channel();
                NettyServer.UserChannels.put(channel.id(),channel);
                NettyServer.Channels.add(channel);
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                Channel channel = ctx.channel();
                channel.close();
                NettyServer.Channels.remove(channel);
                NettyServer.UserChannels.remove(channel.id());
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                cause.printStackTrace();
                ctx.close();
            }
        };
    }
}
