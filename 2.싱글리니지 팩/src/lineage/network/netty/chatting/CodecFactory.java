package lineage.network.netty.chatting;

import static org.jboss.netty.channel.Channels.pipeline;
import lineage.network.codec.chatting.Decoder;
import lineage.network.codec.chatting.Encoder;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

public class CodecFactory implements ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();
		pipeline.addLast("decoder", new Decoder());
		pipeline.addLast("encoder", new Encoder());
		pipeline.addLast("handler", new ProtocolHandler());
		return pipeline;
	}
	
}
