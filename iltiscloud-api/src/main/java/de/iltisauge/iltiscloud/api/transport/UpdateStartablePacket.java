package de.iltisauge.iltiscloud.api.transport;

import java.util.UUID;

import de.iltisauge.iltiscloud.api.Startable.Action;
import de.iltisauge.iltiscloud.api.Startable.Type;
import de.iltisauge.transport.network.IMessageCodec;
import de.iltisauge.transport.network.Message;
import de.iltisauge.transport.utils.PacketUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UpdateStartablePacket extends Message {

	public static final String PACKET_CHANNEL = "update-startable";
	private final Type startableType;
	private final UUID uniqueId;
	private final Action startableAction;
	private final ConfirmationType confirmationType;
	
	public static IMessageCodec<UpdateStartablePacket> CODEC = new IMessageCodec<UpdateStartablePacket>() {
		
		@Override
		public void write(ByteBuf byteBuf, UpdateStartablePacket obj) {
			PacketUtil.writeString(byteBuf, obj.getStartableType().name());
			PacketUtil.writeString(byteBuf, obj.getUniqueId().toString());
			PacketUtil.writeString(byteBuf, obj.getStartableAction().name());
			PacketUtil.writeString(byteBuf, obj.getConfirmationType().name());
		}
		
		@Override
		public UpdateStartablePacket read(ByteBuf byteBuf) {
			final Type type = Type.valueOf(PacketUtil.readString(byteBuf));
			final UUID uniqueId = UUID.fromString(PacketUtil.readString(byteBuf));
			final Action action = Action.valueOf(PacketUtil.readString(byteBuf));
			final ConfirmationType confirmationType = ConfirmationType.valueOf(PacketUtil.readString(byteBuf));
			return new UpdateStartablePacket(type, uniqueId, action, confirmationType);
		}
	};
	
	public boolean send() {
		return send(PACKET_CHANNEL);
	}
	
	public enum ConfirmationType {
		
		UNCONFIRMED,
		CONFIRMED;
	}
}
