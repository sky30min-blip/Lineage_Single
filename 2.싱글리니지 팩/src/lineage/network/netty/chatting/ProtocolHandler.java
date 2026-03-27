package lineage.network.netty.chatting;

import lineage.Main;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;


public class ProtocolHandler extends lineage.network.netty.lineage.ProtocolHandler {

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		// 서버가 종료되는 상황에서는 클라 요청을 차단.
		if(Main.running == false) {
			e.getChannel().close();
			return;
		}
		// 접속방식 체크. 잘못된 접근이면 클라 요청 차단.
		if(isBadClient(e.getChannel())) {
			e.getChannel().close();
			return;
		}
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		//
	}
	
	@Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent me) {
		//
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		//
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		//
	}
	
}
