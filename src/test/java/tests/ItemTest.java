package tests;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import factoryRequest.FactoryRequest;
import factoryRequest.RequestInfo;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import util.ApiConfiguration;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class ItemTest {
    Response response;
    JSONObject body= new JSONObject();
    RequestInfo requestInfo = new RequestInfo();
    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder()
            .setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(
                    SchemaVersion.DRAFTV4
            ).freeze()).freeze();

    @Test
    public void crudItemsTest(){
        body.put("Content","Item1");
        requestInfo.setUrl(ApiConfiguration.CREATE_ITEM);
        requestInfo.setBody(body.toString());

        response= FactoryRequest.make("post").send(requestInfo);
        response.then().body("Content",equalTo(body.get("Content"))).statusCode(200);
        //Validacion con Json schema
        response.then().body(matchesJsonSchemaInClasspath("createItemJsonSchema.json").using(jsonSchemaFactory));

        int idItem=response.then().extract().path("Id");

        body.put("Content","Item1Updated");
        requestInfo.setUrl(String.format(ApiConfiguration.UPDATE_ITEM,idItem));
        requestInfo.setBody(body.toString());
        response= FactoryRequest.make("put").send(requestInfo);
        response.then().body("Content",equalTo(body.get("Content"))).statusCode(200);
        //Validacion con Json schema
        response.then().body(matchesJsonSchemaInClasspath("updateItemJsonSchema.json").using(jsonSchemaFactory));

        requestInfo.setUrl(String.format(ApiConfiguration.READ_ITEM,idItem));
        requestInfo.setBody(body.toString());
        response= FactoryRequest.make("get").send(requestInfo);
        response.then().body("Content",equalTo(body.get("Content"))).statusCode(200);
        //Validacion con Json schema
        response.then().body(matchesJsonSchemaInClasspath("readItemJsonSchema.json").using(jsonSchemaFactory));

        requestInfo.setUrl(String.format(ApiConfiguration.DELETE_ITEM,idItem));
        requestInfo.setBody(body.toString());
        response= FactoryRequest.make("delete").send(requestInfo);
        response.then().body("Content",equalTo(body.get("Content"))).statusCode(200);
        //Validacion con Json schema
        response.then().body(matchesJsonSchemaInClasspath("deleteItemJsonSchema.json").using(jsonSchemaFactory));

    }
}
