package org.doogle.codec;

import io.github.cbartosiak.bson.codecs.jsr310.dayofweek.DayOfWeekAsInt32Codec;
import io.github.cbartosiak.bson.codecs.jsr310.dayofweek.DayOfWeekAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.duration.DurationAsDecimal128Codec;
import io.github.cbartosiak.bson.codecs.jsr310.duration.DurationAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.duration.DurationAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.instant.InstantAsDateTimeCodec;
import io.github.cbartosiak.bson.codecs.jsr310.instant.InstantAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.instant.InstantAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localdate.LocalDateAsDateTimeCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localdate.LocalDateAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localdate.LocalDateAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localdatetime.LocalDateTimeAsDateTimeCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localdatetime.LocalDateTimeAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localdatetime.LocalDateTimeAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localtime.LocalTimeAsDateTimeCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localtime.LocalTimeAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localtime.LocalTimeAsInt64Codec;
import io.github.cbartosiak.bson.codecs.jsr310.localtime.LocalTimeAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.month.MonthAsInt32Codec;
import io.github.cbartosiak.bson.codecs.jsr310.month.MonthAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.monthday.MonthDayAsDecimal128Codec;
import io.github.cbartosiak.bson.codecs.jsr310.monthday.MonthDayAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.monthday.MonthDayAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.offsetdatetime.OffsetDateTimeAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.offsetdatetime.OffsetDateTimeAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.offsettime.OffsetTimeAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.offsettime.OffsetTimeAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.period.PeriodAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.period.PeriodAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.year.YearAsInt32Codec;
import io.github.cbartosiak.bson.codecs.jsr310.yearmonth.YearMonthAsDecimal128Codec;
import io.github.cbartosiak.bson.codecs.jsr310.yearmonth.YearMonthAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.yearmonth.YearMonthAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.zoneddatetime.ZonedDateTimeAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.zoneddatetime.ZonedDateTimeAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.zoneid.ZoneIdAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.zoneoffset.ZoneOffsetAsInt32Codec;
import io.github.cbartosiak.bson.codecs.jsr310.zoneoffset.ZoneOffsetAsStringCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.InstantCodec;
import org.bson.codecs.jsr310.LocalDateCodec;
import org.bson.codecs.jsr310.LocalDateTimeCodec;
import org.bson.codecs.jsr310.LocalTimeCodec;

import java.util.HashMap;
import java.util.Map;

public class Jsr310CodecProvider implements CodecProvider {
    private static final Map<Class<?>, Codec<?>> JSR310_CODEC_MAP = new HashMap<>();

    static {
        try {
            Class.forName("java.time.Instant");
            putCodec(new InstantCodec());
            putCodec(new LocalDateCodec());
            putCodec(new LocalDateTimeCodec());
            putCodec(new LocalTimeCodec());
            putCodec(new DayOfWeekAsInt32Codec());
            putCodec(new DayOfWeekAsStringCodec());
            putCodec(new DurationAsDecimal128Codec());
            putCodec(new DurationAsDocumentCodec());
            putCodec(new DurationAsStringCodec());
            putCodec(new InstantAsDateTimeCodec());
//            putCodec(new InstantAsDocumentCodec());
//            putCodec(new InstantAsStringCodec());
            putCodec(new LocalDateAsDateTimeCodec());
//            putCodec(new LocalDateAsDocumentCodec());
//            putCodec(new LocalDateAsStringCodec());
            putCodec(new LocalDateTimeAsDateTimeCodec());
//            putCodec(new LocalDateTimeAsDocumentCodec());
//            putCodec(new LocalDateTimeAsStringCodec());
            putCodec(new LocalTimeAsDateTimeCodec());
//            putCodec(new LocalTimeAsDocumentCodec());
//            putCodec(new LocalTimeAsInt64Codec());
//            putCodec(new LocalTimeAsStringCodec());
            putCodec(new MonthAsInt32Codec());
//            putCodec(new MonthAsStringCodec());
            putCodec(new MonthDayAsDecimal128Codec());
//            putCodec(new MonthDayAsDocumentCodec());
//            putCodec(new MonthDayAsStringCodec());
            putCodec(new OffsetDateTimeAsDocumentCodec());
//            putCodec(new OffsetDateTimeAsStringCodec());
            putCodec(new OffsetTimeAsDocumentCodec());
//            putCodec(new OffsetTimeAsStringCodec());
//            putCodec(new PeriodAsDocumentCodec());
//            putCodec(new PeriodAsStringCodec());
//            putCodec(new YearAsInt32Codec());
//            putCodec(new YearMonthAsDecimal128Codec());
//            putCodec(new YearMonthAsDocumentCodec());
//            putCodec(new YearMonthAsStringCodec());
//            putCodec(new ZonedDateTimeAsDocumentCodec());
            putCodec(new ZonedDateTimeCodec());
//            putCodec(new ZonedDateTimeAsStringCodec());
            putCodec(new ZoneIdAsStringCodec());
            putCodec(new ZoneOffsetAsInt32Codec());
//            putCodec(new ZoneOffsetAsStringCodec());
        } catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    private static void putCodec(Codec<?> codec) {
        JSR310_CODEC_MAP.put(codec.getEncoderClass(), codec);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        return (Codec<T>) JSR310_CODEC_MAP.get(clazz);
    }

    @Override
    public String toString() {
        return "Jsr310CodecProvider{}";
    }
}