package me.whereareiam.socialismus.module.bubbler.common.worker.container;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import me.whereareiam.socialismus.api.ComponentUtil;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.input.serializer.SerializationService;
import me.whereareiam.socialismus.api.model.Worker;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

@Singleton
public class ContentArranger {
  private static final String MESSAGE_PLACEHOLDER = "{message}";
  private static final String PLAYER_NAME_PLACEHOLDER = "{playerName}";

  private final LoggingHelper loggingHelper;
  private final SerializationService serializer;

  @Inject
  public ContentArranger(
      LoggingHelper loggingHelper,
      WorkerProcessor<BubbleMessage> workerProcessor,
      SerializationService serializer) {
    this.loggingHelper = loggingHelper;
    this.serializer = serializer;

    workerProcessor.addWorker(new Worker<>(this::arrangeContent, 150, false, false));
  }

  public BubbleMessage arrangeContent(BubbleMessage bubbleMessage) {
    loggingHelper.debug("Arranging content for " + bubbleMessage.getSender().getUsername());

    String content = ComponentUtil.toString(bubbleMessage.getContent());

    List<String> lines = formatContent(bubbleMessage, content);
    List<BubbleLine> bubbleLines = createBubbleLines(bubbleMessage, lines);

    bubbleMessage.setLines(bubbleLines);

    return bubbleMessage;
  }

  private List<String> formatContent(BubbleMessage bubbleMessage, String content) {
    List<String> lines = new ArrayList<>();

    Bubble bubble = bubbleMessage.getBubble();
    int maxLineWidth = bubble.getDisplay().getMaxLineWidth();
    String[] words = content.split(" ");
    String messageFormat = bubble.getFormat().getFormat();

    StringBuilder currentLine = new StringBuilder();
    for (String word : words) {
      if (currentLine.length() + word.length() > maxLineWidth) {
        if (word.length() > maxLineWidth) {
          while (word.length() > maxLineWidth) {
            int partLength = maxLineWidth - currentLine.length();
            if (partLength <= 0) {
              lines.add(messageFormat.replace(MESSAGE_PLACEHOLDER, currentLine.toString().trim()));
              currentLine = new StringBuilder();
              partLength = maxLineWidth;
            }

            String part = word.substring(0, partLength);
            word = word.substring(partLength);

            currentLine.append(part).append(bubble.getFormat().getSeparatorFormat());
            lines.add(messageFormat.replace(MESSAGE_PLACEHOLDER, currentLine.toString().trim()));
            currentLine = new StringBuilder();
          }
        } else {
          lines.add(messageFormat.replace(MESSAGE_PLACEHOLDER, currentLine.toString().trim()));
          currentLine = new StringBuilder();
        }
      }

      currentLine.append(word).append(" ");
    }
    if (!currentLine.isEmpty())
      lines.add(messageFormat.replace(MESSAGE_PLACEHOLDER, currentLine.toString().trim()));

    return lines;
  }

  private List<BubbleLine> createBubbleLines(BubbleMessage bubbleMessage, List<String> lines) {
    Bubble bubble = bubbleMessage.getBubble();
    int maxLinesCount = bubble.getDisplay().getMaxLinesCount();

    List<BubbleLine> bubbleLines = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      String lineContent = lines.get(i);
      if (i == lines.size() - 1 && lines.size() > maxLinesCount) {
        lineContent += bubble.getFormat().getQueuedFormat();
      }
      bubbleLines.add(
          BubbleLine.builder()
              .content(serializer.format(bubbleMessage.getSender(), lineContent))
              .build());
    }

    applyFormats(bubbleMessage, bubbleLines);

    return bubbleLines;
  }

  private void applyFormats(BubbleMessage bubbleMessage, List<BubbleLine> bubbleLines) {
    if (bubbleLines.isEmpty()) return;

    Bubble bubble = bubbleMessage.getBubble();
    String initialFormat =
        bubble
            .getFormat()
            .getInitialFormat()
            .replace(PLAYER_NAME_PLACEHOLDER, bubbleMessage.getSender().getUsername());
    String finalFormat = bubble.getFormat().getFinalFormat();

    applyFormatToLine(initialFormat, bubbleLines, bubbleMessage.getSender(), true);
    applyFormatToLine(finalFormat, bubbleLines, bubbleMessage.getSender(), false);
  }

  private void applyFormatToLine(
      String format, List<BubbleLine> bubbleLines, DummyPlayer sender, boolean isInitial) {
    if (format.contains("\n")) {
      String[] parts = format.split("\n");
      List<BubbleLine> newLines = new ArrayList<>();

      for (String part : parts) {
        newLines.add(BubbleLine.builder().content(serializer.format(sender, part)).build());
      }

      if (isInitial) {
        newLines.addAll(bubbleLines);
      } else {
        newLines.addAll(0, bubbleLines);
      }

      bubbleLines.clear();
      bubbleLines.addAll(newLines);
    } else {
      int index = isInitial ? 0 : bubbleLines.size() - 1;
      BubbleLine line = bubbleLines.get(index);
      if (isInitial) {
        line =
            BubbleLine.builder()
                .content(serializer.format(sender, format).append(line.getContent()))
                .build();
      } else {
        line =
            BubbleLine.builder()
                .content(line.getContent().append(serializer.format(sender, format)))
                .build();
      }

      bubbleLines.set(index, line);
    }
  }
}
