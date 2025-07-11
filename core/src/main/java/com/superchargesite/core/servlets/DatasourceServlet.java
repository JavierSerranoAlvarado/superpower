package com.superchargesite.core.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.resourceTypes=" + "/apps/dropDownLIsting"
        })
public class DatasourceServlet extends SlingSafeMethodsServlet {
     private static Logger LOGGER = LoggerFactory.getLogger(DatasourceServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) {
                            LOGGER.info("Enter on doGet");
        try {
            ResourceResolver resourceResolver = request.getResourceResolver();
            List<KeyValue> dropDownList = new ArrayList<>();
            String apiUrl = "http://pokeapi.co/api/v2/pokemon-species/";
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(apiUrl);
                try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                    String responseString = EntityUtils.toString(httpResponse.getEntity());

                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.has("results") && jsonObject.get("results") instanceof JSONArray) {
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject pokemonObject = jsonArray.getJSONObject(i);
                            
                            // 3. Obtener la propiedad de cada objeto
                            String pokemonName = pokemonObject.getString("name");
                            dropDownList.add(new KeyValue(pokemonName, pokemonName));
                        }
                    }
                }
            }
                @SuppressWarnings("unchecked")
                DataSource ds =
                        new SimpleDataSource(
                                new TransformIterator(
                                        dropDownList.iterator(),
                                        input -> {
                                            KeyValue keyValue = (KeyValue) input;
                                            ValueMap vm = new ValueMapDecorator(new HashMap<>());
                                            vm.put("value", keyValue.key);
                                            vm.put("text", keyValue.value);
                                            return new ValueMapResource(
                                                    resourceResolver, new ResourceMetadata(),
                                                    "nt:unstructured", vm);
                                        }));
                request.setAttribute(DataSource.class.getName(), ds);

        } catch (Exception e) {
            LOGGER.error("Error in Get Drop Down Values", e);
        }
    }

    private class KeyValue {

        /**
         * key property.
         */
        private String key;
        /**
         * value property.
         */
        private String value;

        /**
         * constructor instance intance.
         *
         * @param newKey   -
         * @param newValue -
         */
        private KeyValue(final String newKey, final String newValue) {
            this.key = newKey;
            this.value = newValue;
        }
    }
}
