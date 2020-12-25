package cn.layne666.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathResult {
    private Map<Path, Path> fromTo = new HashMap<>();
    private List<String> unsettledList = new ArrayList<>();

    public void put(Path from, Path to) {
        fromTo.put(from, to);
    }

    public void addUnsettled(String unsettledFilePath) {
        unsettledList.add(unsettledFilePath);
    }

    public Map<Path, Path> getFromTo() {
        return fromTo;
    }

    public void setFromTo(Map<Path, Path> fromTo) {
        this.fromTo = fromTo;
    }

    public List<String> getUnsettledList() {
        return unsettledList;
    }

    public void setUnsettledList(List<String> unsettledList) {
        this.unsettledList = unsettledList;
    }
}
