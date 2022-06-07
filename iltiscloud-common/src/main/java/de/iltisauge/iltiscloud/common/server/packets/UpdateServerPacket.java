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
public class UpdateServerPacket extends Message {

	private final UUID serverId;
	private final UpdateServerAction action;
	/**
	 * Can be null if UpdateServerAction is not {@link UpdateServerAction#UPDATE_DATA}.
	 */
	private final String updatedField;
	
	public static final IMessageCodec<UpdateServerPacket> CODEC = new IMessageCodec<UpdateServerPacket>() {
		
		@Override
		public void write(ByteBuf byteBuf, UpdateServerPacket obj) {
			PacketUtil.writeString(byteBuf, obj.getServerId().toString());
			PacketUtil.writeString(byteBuf, obj.getAction().name());
			PacketUtil.writeString(byteBuf, obj.getUpdatedField() == null ? "" : obj.getUpdatedField());
		}
		
		@Override
		public UpdateServerPacket read(ByteBuf byteBuf) {
			final UUID serverId = UUID.fromString(PacketUtil.readString(byteBuf));
			System.out.println("serverId=" + serverId.toString());
			final UpdateServerAction action = UpdateServerAction.valueOf(PacketUtil.readString(byteBuf));
			System.out.println("action=" + action);
			final String updatedField = PacketUtil.readString(byteBuf);
			System.out.println("updatedField=" + updatedField);
			return new UpdateServerPacket(serverId, action, updatedField == "" ? null : updatedField);
		}
	};
	
	public enum UpdateServerAction {
		
		CREATE,
		DELETE,
		UPDATE_DATA;
		
	}
}
