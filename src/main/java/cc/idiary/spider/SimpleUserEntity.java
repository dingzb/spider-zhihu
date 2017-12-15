package cc.idiary.spider;

public class SimpleUserEntity {
    private String urlToken;
    private String name;

    public String getUrlToken() {
        return urlToken;
    }

    public void setUrlToken(String urlToken) {
        this.urlToken = urlToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SimpleUserEntity{" +
                "urlToken='" + urlToken + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
