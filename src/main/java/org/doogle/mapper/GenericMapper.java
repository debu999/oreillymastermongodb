package org.doogle.mapper;

import java.security.SecureRandom;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.doogle.data.BlockData;
import org.doogle.data.TransactionData;
import org.doogle.entity.BlockEntity;
import org.doogle.entity.TransactionEntity;
import org.doogle.entity.embedded.Group;


public class GenericMapper {

  public static List<String> tags = List.of("scam", "ico");
  public static SecureRandom random = new SecureRandom();

  @SneakyThrows
  public static List<TransactionEntity> getTransactionsFromTransactionData(
      TransactionData transactionData) {

    ZonedDateTime now = ZonedDateTime.now();

    return transactionData.getResult().getExtractorData().getData().get(0).getGroup().stream().map(
        g -> TransactionEntity.builder().from(g.getFrom().get(0).getText())
            .to(StringUtils.right(g.getTo().get(0).getHref(),
                g.getTo().get(0).getHref().length() - 29)).txHash(g.getTxHash().get(0).getText())
            .txfee(Double.valueOf(g.getTxFee().get(0).getText()))
            .value(Double.valueOf(g.getValue().get(0).getText().split(" ")[0]))
            .block(Integer.parseInt(g.getBlock().get(0).getText()))
            .timestamp(getRandomDateTimeInRange(now.minusDays(3), now)).tags(getRandomTags())
            .build()).toList();
  }

  @SneakyThrows
  public static BlockEntity getBlocksFromBlockData(BlockData blockData) {

    ZonedDateTime now = ZonedDateTime.now();

    List<Group> groupsBlockData = blockData.getResult().getExtractorData().getData().get(0)
        .getGroup();
    return BlockEntity.builder()
        .height(Integer.parseInt(groupsBlockData.get(0).getHeight().get(0).getText()))
        .transactionsNumber(Integer.parseInt(
            groupsBlockData.get(1).getTransactions().get(0).getText().split(" ")[0]))
        .internalTransactionsNumber(Integer.parseInt(
            groupsBlockData.get(1).getTransactions().get(0).getText().split(" ")[3]))
        .difficulty(
            NumberFormat.getNumberInstance(Locale.US)
                .parse(groupsBlockData.get(6).getDifficulty().get(0).getText()).longValue())
        .hash(groupsBlockData.get(2).getHash().get(0).getText()).gasUsed(
            NumberFormat.getNumberInstance(Locale.US)
                .parse(groupsBlockData.get(10).getGasUsed().get(0).getText()).longValue())
        .timestamp(getRandomDateTimeInRange(now.minusDays(3), now)).build();
  }

  public static ZonedDateTime getRandomDateTimeInRange(ZonedDateTime from, ZonedDateTime to) {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(
            random.nextLong(from.toInstant().toEpochMilli(), to.toInstant().toEpochMilli() + 1)),
        ZoneOffset.UTC);
  }

  /**
   * adding random tags in our documents, simulating our classification of a transaction as scam or
   * ico
   *
   * @return
   */
  public static List<String> getRandomTags() {
    List<String> randomTags = new ArrayList<>();

    IntStream.range(1, 3).boxed().forEach(i -> randomTags.add(tags.get(random.nextInt(2))));
    return randomTags;
  }

}
