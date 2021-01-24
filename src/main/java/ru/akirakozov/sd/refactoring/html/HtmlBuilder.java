package ru.akirakozov.sd.refactoring.html;

public class HtmlBuilder {
    private StringBuilder sb = new StringBuilder();

    public void add(String line) {
        sb.append(line);
    }

    public void addLn(String line) {
        sb.append(line).append("</br>");
    }

    public void addH1(String line) {
        sb.append("<h1>").append(line).append("</h1>");
    }

    public String buildHtml() {
        return "<html><body>" + sb.toString() + "</body></html>";
    }
}