package KChat;

import KChat.Common.Constants;
import KChat.Configuration.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public final class NettyServer {
    public final static Map<String, Channel> UserChannels = new ConcurrentHashMap<>();
    public final static Map<String,ChannelGroup> GroupChannels = new ConcurrentHashMap<>();
    public static final AttributeKey<String> UserIdAttr = AttributeKey.newInstance(Constants.WebSocketUserId);
    public static final AttributeKey<String> TokenAttr = AttributeKey.newInstance(Constants.JwtTokenSign);

    private final SimpleChannelInboundHandler<WebSocketFrame> handler;
    private final NettyConfig nettyConfig;

    @Autowired
    public NettyServer(SimpleChannelInboundHandler<WebSocketFrame>handler, NettyConfig nettyConfig){
        this.handler = handler;
        this.nettyConfig = nettyConfig;
    }

    public void initAndRun(){
        nettyConfig.channelInit(handler);
    }
}
