package com.futurice.project.models.pojo;

import java.util.List;

public class SearchEngineResults {
    public List<Topic> RelatedTopics;

    public static class Topic {
        public String FirstURL;
        public String Text;
        public List<Topic> Topics;
    }
}
