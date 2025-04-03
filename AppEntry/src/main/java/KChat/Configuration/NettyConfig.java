package KChat.Configuration;

import KChat.Common.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "netty")
@Getter
@Setter
public class NettyConfig {
    private String path;
    private Integer port;
    private Integer maxContentLength;
    private Long handshakeTimeOut;

    public void channelInit(SimpleChannelInboundHandler<WebSocketFrame> webSocketHandler,ChannelInboundHandlerAdapter adapter){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // HTTP编解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 支持大数据流
                            pipeline.addLast(new ChunkedWriteHandler());
                            // 聚合HTTP消息
                            pipeline.addLast(new HttpObjectAggregator(maxContentLength));
                            // WebSocket协议处理器
                            pipeline.addLast(new WebSocketServerProtocolHandler(path,null,
                                    true,maxContentLength,true,true,handshakeTimeOut));
                            // 自定义WebSocket消息处理器
                            pipeline.addLast(new IdleStateHandler(Constants.HeartbeatTimeout,0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(adapter);
                            pipeline.addLast(webSocketHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口并启动服务器
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("WebSocket server started on port " + port);
            future.channel().closeFuture().sync();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
