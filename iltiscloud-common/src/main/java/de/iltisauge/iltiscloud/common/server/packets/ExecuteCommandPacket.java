package de.iltisauge.iltiscloud.common.server.packets;

import java.util.UUID;

import de.iltisauge.transport.network.IMessageCodec;
import de.iltisauge.transport.network.Message;
import de.iltisauge.transport.utils.PacketUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class ExecuteCommandPacket extends Message {

	private final UUID serverId;
	private final String command;
	
	public static final IMessageCodec<ExecuteCommandPacket> CODEC = new IMessageCodec<ExecuteCommandPacket>() {
		
		@Override
		public void write(ByteBuf byteBuf, ExecuteCommandPacket obj) {
			PacketUtil.writeString(byteBuf, obj.getServerId().toString());
			PacketUtil.writeString(byteBuf, obj.getCommand());
		}
		
		@Override
		public ExecuteCommandPacket read(ByteBuf byteBuf) {
			final UUID serverId = UUID.fromString(PacketUtil.readString(byteBuf));
			final String command = PacketUtil.readString(byteBuf);
			return new ExecuteCommandPacket(serverId, command);
		}
	};
}
