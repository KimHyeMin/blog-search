package com.lily.trends;


import com.lily.trends.dto.FrequentKeyword;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@EnableScheduling
@Component
public class LocalKeywordService implements KeywordService {

  private final HashMap<String, FrequentKeyword> storage = new HashMap<>();
  private static final int TOP_LIMIT = 100;

  private List<String> buffer = new ArrayList<>();
  private final Object bufferLock = new Object();
  private List<FrequentKeyword> answer = new ArrayList<>();
  private final Object answerLock = new Object();

  @Override
  public List<FrequentKeyword> getTopKeywords(final int top) {
    if (top <= 0) {
      throw new RuntimeException("Top num must greater than 0, but received top: " + top);
    }

    synchronized (answerLock) {
      return answer.subList(0, Math.min(top, answer.size()));
    }
  }

  @Override
  public void increaseCount(final String keywords) {
    synchronized (bufferLock) {
      buffer.add(keywords);
    }
  }

  @Scheduled(fixedDelay = 1000)
  public void refreshMap() {
    List<String> commitBuffer;

    synchronized (bufferLock) {
      commitBuffer = buffer;
      buffer = new ArrayList<>();
    }

    if (commitBuffer.isEmpty()) {
      return;
    }

    log.info("refresh ranking storage....");
    for (String keyword : commitBuffer) {
      FrequentKeyword frequentKeyword = storage.get(keyword);
      if (frequentKeyword == null) {
        storage.put(keyword, new FrequentKeyword(keyword, 1));
      } else {
        frequentKeyword.setCount(frequentKeyword.getCount() + 1);
      }
    }

    List<FrequentKeyword> refreshedAnswer = storage
        .values()
        .stream()
        .sorted((a,b) -> b.getCount().compareTo(a.getCount()))
        .limit(TOP_LIMIT)
        .collect(Collectors.toList());

    synchronized (answerLock) {
      answer = refreshedAnswer;
    }
  }

}
