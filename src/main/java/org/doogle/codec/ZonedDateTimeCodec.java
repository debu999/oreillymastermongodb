package org.doogle.codec;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;

import java.time.Instant;

import static java.lang.String.format;

/**
 * ZonedDateTime Codec.
 *
 * <p>
 * Encodes and decodes {@code ZonedDateTime} objects to and from {@code DateTime}.
 * Data is extracted via {@link Instant#toEpochMilli()} and stored to millisecond accuracy.
 * </p>
 */
public class ZonedDateTimeCodec extends DateTimeBasedCodec<ZonedDateTime> {

    @Override
    public ZonedDateTime decode(final BsonReader reader, final DecoderContext decoderContext) {
        return Instant.ofEpochMilli(validateAndReadDateTime(reader)).atZone(ZoneOffset.UTC);
    }

    @Override
    public void encode(final BsonWriter writer, final ZonedDateTime value, final EncoderContext encoderContext) {
        try {
            writer.writeDateTime(value.toInstant().toEpochMilli());
        } catch (ArithmeticException e) {
            throw new CodecConfigurationException(format("Unsupported ZonedDateTime value '%s' could not be converted to milliseconds: %s",
                    value, e.getMessage()), e);
        }
    }

    @Override
    public Class<ZonedDateTime> getEncoderClass() {
        return ZonedDateTime.class;
    }
}