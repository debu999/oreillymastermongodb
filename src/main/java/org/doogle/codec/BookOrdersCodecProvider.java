package org.doogle.codec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.doogle.model.BookOrdersModel;

public class BookOrdersCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz.equals(BookOrdersModel.class)) {
            return (Codec<T>) new BookOrdersCodec();
        }
        return null;
    }

}