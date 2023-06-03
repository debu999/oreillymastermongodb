package org.doogle.codec;

import com.mongodb.MongoClientSettings;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.doogle.model.BookOrdersModel;

import java.util.UUID;

public class BookOrdersCodec implements CollectibleCodec<BookOrdersModel> {

    private final Codec<Document> documentCodec;

    public BookOrdersCodec() {
        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }

    @Override
    public void encode(BsonWriter writer, BookOrdersModel bookOrdersModel, EncoderContext encoderContext) {
        Document doc = new Document();

        if (bookOrdersModel.getId() != null) {
            doc.put("_id", bookOrdersModel.getId());
        }
        doc.put("name", bookOrdersModel.getName());
        doc.put("isbn", bookOrdersModel.getIsbn());
        doc.put("price", bookOrdersModel.getPrice());
        documentCodec.encode(writer, doc, encoderContext);
    }

    @Override
    public Class<BookOrdersModel> getEncoderClass() {
        return BookOrdersModel.class;
    }

    @Override
    public BookOrdersModel generateIdIfAbsentFromDocument(BookOrdersModel document) {
        if (!documentHasId(document)) {
            document.setId(UUID.randomUUID().toString());
        }
        return document;
    }

    @Override
    public boolean documentHasId(BookOrdersModel document) {
        return document.getId() != null;
    }

    @Override
    public BsonValue getDocumentId(BookOrdersModel document) {
        return new BsonString(document.getId());
    }

    @Override
    public BookOrdersModel decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(reader, decoderContext);
        BookOrdersModel bookOrdersModel = new BookOrdersModel();
        if (document.getString("id") != null) {
            bookOrdersModel.setId(document.getString("id"));
        }
        bookOrdersModel.setName(document.getString("name"));
        bookOrdersModel.setIsbn(document.getInteger("isbn"));
        bookOrdersModel.setPrice(document.getDouble("price"));
        return bookOrdersModel;
    }
}