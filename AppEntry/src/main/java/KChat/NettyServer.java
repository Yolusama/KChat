package KChat;

import KChat.Configuration.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public final class NettyServer {
    public final static Map<ChannelId, Channel> UserChannels = new ConcurrentHashMap<>();
    public final static ChannelGroup Channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final SimpleChannelInboundHandler<WebSocketFrame> handler;
    private final NettyConfig nettyConfig;

    @Autowired
    public NettyServer(SimpleChannelInboundHandler<WebSocketFrame>handler,NettyConfig nettyConfig){
        this.handler = handler;
        this.nettyConfig = nettyConfig;
    }

    public void initAndRun(){
        nettyConfig.channelInit(handler);
    }
}
