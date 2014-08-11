package com.futurice.project.models.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchEngineResults {
    public List<Topic> RelatedTopics;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Topic {
        public String FirstURL;
        public String Text;
        public List<Topic> Topics;
    }
}
