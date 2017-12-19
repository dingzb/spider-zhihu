package cc.idiary.spider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;

public class UserParser {

    private JsonNode dataNode;
    private JsonNode pagingNode;

    private UserParser(){}

    public static UserParser createParser(String content){

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;

        try {
            node = mapper.readTree(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (node == null || node.isNull()) {
            System.err.println("Have no valid content.");
            return null;
        }

        UserParser userParser = new UserParser();

        userParser.pagingNode = node.get("paging");
        userParser.dataNode = node.get("data");

        return userParser;
    }

    public boolean isEnd() {
        JsonNode isEndNode = pagingNode.get("is_end");
        if (isEndNode == null || isEndNode.isNull()) {
            return false;
        } else {
            return isEndNode.booleanValue();
        }
    }

    public String nextUrl() {
        JsonNode nextNode = pagingNode.get("next");
        if (nextNode == null || nextNode.isNull()) {
            return null;
        } else {
            return nextNode.textValue();
        }
    }

    public Iterator<UserConstruction> getConstruction() {
        Iterator<JsonNode> nodeIterator = dataNode.elements();
        return new Iterator<UserConstruction>() {
            @Override
            public boolean hasNext() {
                return nodeIterator.hasNext();
            }

            @Override
            public UserConstruction next() {
                JsonNode nextNode = nodeIterator.next();
                return new UserConstruction(nextNode);
            }
        };
    }

    private boolean isNull(JsonNode node) {
        return (node == null || node.isNull());
    }

    public class UserConstruction {
        private JsonNode userNode;

        public UserConstruction(JsonNode userNode) {
            this.userNode = userNode;
        }

        public String getId() {
            JsonNode idNode = userNode.get("id");
            if (isNull(idNode)) {
                return null;
            } else {
                return idNode.textValue();
            }
        }

        public UserEntity getEntity() {
            UserEntity user = new UserEntity();

            user.setId(getId());

            JsonNode nameNode = userNode.get("name");
            if (!isNull(nameNode)){
                user.setName(nameNode.textValue());
            }

            JsonNode urlTokenNode = userNode.get("url_token");
            if (!isNull(urlTokenNode)) {
                user.setUrlToken(urlTokenNode.textValue());
            }

            return user;
        }
    }

}


