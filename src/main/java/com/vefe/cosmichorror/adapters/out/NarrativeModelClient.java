package com.vefe.cosmichorror.adapters.out;

import dev.langchain4j.model.chat.ChatModel;

final class NarrativeModelClient {
  private final ChatModel model;

  NarrativeModelClient(ChatModel model) {
    this.model = model;
  }

  String complete(String prompt) {
    return model.chat(prompt);
  }
}

