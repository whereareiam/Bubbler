package me.whereareiam.socialismus.module.bubbler.common.worker.container;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.ComponentUtil;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.model.Worker;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

@Singleton
public class ContentTimeEvaluator {
  private final LoggingHelper loggingHelper;
  private final Provider<Settings> settings;

  @Inject
  public ContentTimeEvaluator(
      LoggingHelper loggingHelper,
      WorkerProcessor<BubbleMessage> workerProcessor,
      Provider<Settings> settings) {
    this.loggingHelper = loggingHelper;
    this.settings = settings;

    workerProcessor.addWorker(new Worker<>(this::evaluateTime, 200, true, false));
  }

  public BubbleMessage evaluateTime(BubbleMessage bubbleMessage) {
    loggingHelper.debug("Evaluating display time for " + bubbleMessage.getSender().getUsername());

    bubbleMessage
        .getLines()
        .forEach(
            line -> {
              long displayTime =
                  calculateDisplayTime(
                      ComponentUtil.toPlain(line.getContent()), bubbleMessage.getBubble());
              line.setDisplayTime(displayTime);
            });

    if (settings.get().getLevel() >= 3)
      loggingHelper.debug(
          "Display time for "
              + bubbleMessage.getSender().getUsername()
              + " is "
              + bubbleMessage.getLines().stream().mapToLong(BubbleLine::getDisplayTime).sum());

    return bubbleMessage;
  }

  private long calculateDisplayTime(String content, Bubble bubble) {
    int symbolCount = content.length();
    double timePerSymbol = bubble.getDisplay().getTimePerSymbol();
    double minimumTime = bubble.getDisplay().getMinimumTime();

    long calculatedTime = (long) (symbolCount * timePerSymbol);
    return Math.max(calculatedTime, (long) minimumTime);
  }
}
