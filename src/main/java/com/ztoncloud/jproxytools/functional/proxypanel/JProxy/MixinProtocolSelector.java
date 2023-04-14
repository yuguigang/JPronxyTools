package com.ztoncloud.jproxytools.functional.proxypanel.JProxy;

import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.handler.SocksHandler.SocksServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.cert.CertificateException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多协议协议选择器 COCKS4A SOCKS5 HTTP HTTPS
 *
 * @author yugang
 * @date 2022/11/16
 */

public class MixinProtocolSelector extends ByteToMessageDecoder {


    private  static final Logger logger = LoggerFactory.getLogger(MixinProtocolSelector.class);

    // socks4 proxy protocol
    private static final byte SOCKS4A = 0x4;
    // socks5 proxy protocol
    private static final byte SOCKS5 = 0x5;
    // hypertext transport protocol
    private static final byte GET = 0x47;
    private static final byte POST_AND_PUT = 0x50;
    private static final byte DELETE = 0x44;
    private static final byte OPTIONS = 0x4f;
    private static final byte HEAD = 0x48;
    private static final byte CONNECT = 0x43;
    private static final byte TRACE = 0x54;
    // transport layer security protocol
    private static final byte TLS = 0x16;

    //private final SocksMessageHandler socksMessageHandler = new SocksMessageHandler();
   // private final HttpMessageHandler httpMessageHandler = new HttpMessageHandler();
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int readerIndex = in.readerIndex();

        if (in.writerIndex() == readerIndex) {
            return;
        }
        ChannelPipeline pipeline = ctx.pipeline();
        final byte ver = in.getByte(readerIndex);
        switch (ver) {
            case SOCKS4A ,SOCKS5 -> {
                logKnownVersion(ctx, "SOCKS "+ver);
                pipeline.addLast(
                        new SocksPortUnificationServerHandler() ,
                        SocksServerHandler.INSTANCE
                        );


                //pipeline.addLast(Names.SOCKS_MESSAGE,new SocksServerInitializer());
                //pipeline.addLast(Names.DECODER, new Socks4ServerDecoder());//解码
                //pipeline.addLast(Names.RESPONSE_ENCODER, Socks4ServerEncoder.INSTANCE);//编码
                //pipeline.addLast(Names.SOCKS_MESSAGE,socksMessageHandler);

            }
            /*
            case SOCKS5 -> {
                logKnownVersion(ctx, SocksVersion.SOCKS5.name()+" "+ver);
                pipeline.addLast(HandlerNames.DECODER, new Socks5InitialRequestDecoder());
                pipeline.addLast(HandlerNames.RESPONSE_ENCODER, Socks5ServerEncoder.DEFAULT);
                pipeline.addLast(HandlerNames.RELAY, SocksServerHandler.INSTANCE);
            }

             */
            /*
            case GET, POST_AND_PUT, DELETE, OPTIONS, HEAD, TRACE, CONNECT -> {
                logKnownVersion(ctx, "HTTP "+ver);
                pipeline.addLast(JProxyNames.DECODER, new  HttpServerCodec());
                pipeline.addLast(JProxyNames.HTTP_MESSAGE,httpMessageHandler);

            }
            case TLS -> {
                logKnownVersion(ctx, "HTTPS");
                pipeline.addLast(JProxyNames.SSL,newSslHandler(ctx.channel()));
                pipeline.addLast(JProxyNames.DECODER, new  HttpServerCodec());
                pipeline.addLast(JProxyNames.HTTP_MESSAGE,httpMessageHandler);
            }

             */
            default -> {
                logKnownVersion(ctx, "Unknown version "+ver);
                in.skipBytes(in.readableBytes());
                ctx.close();
                return;
            }
        }
        pipeline.remove(this);
    }

    private SslHandler newSslHandler(Channel channel) {
        String certFile = System.getenv("CERT_FILE");
        String keyFile = System.getenv("KEY_FILE");
        try {
            SslContext sslContext;
            if (certFile != null && keyFile != null) {
                sslContext = SslContextBuilder.forServer(new File(certFile), new File(keyFile)).build();
            } else {
                SelfSignedCertificate certificate = CertificateHolder.certificate;
                sslContext = SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();
            }
            return sslContext.newHandler(channel.alloc());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void logKnownVersion(ChannelHandlerContext ctx, String version) {
        logger.info("{} 入站协议 version:  ({}) ", ctx.channel(), version );

    }

    private static class CertificateHolder {
        private static final SelfSignedCertificate certificate;

        static {
            try {
                certificate = new SelfSignedCertificate();
                File dir = new File(System.getProperty("user.home"), "cert");
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new IOException("cannot generate certificate and key because dir is not generated!");
                }
                Path certPath = dir.toPath().resolve("server_crt.pem");
                Path keyPath = dir.toPath().resolve("server_pkcs8_key.pem");
                logger.info("certificate and key files is generated:\ncertificate: {}\nprivate key: {}\n", certPath, keyPath);
                Files.copy(certificate.certificate().toPath(), certPath, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(certificate.privateKey().toPath(), keyPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (CertificateException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

