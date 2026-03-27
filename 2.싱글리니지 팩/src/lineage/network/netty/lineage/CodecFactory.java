package lineage.network.netty.lineage;

import static org.jboss.netty.channel.Channels.pipeline;
import lineage.network.codec.lineage.Decoder;
import lineage.network.codec.lineage.Encoder;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

public final class CodecFactory implements ChannelPipelineFactory {
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();
		
		pipeline.addLast("decoder", new Decoder());
		pipeline.addLast("encoder", new Encoder());
		pipeline.addLast("handler", new ProtocolHandler());
		
		return pipeline;
	}
	
}
