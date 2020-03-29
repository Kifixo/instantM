package info.androidhive.loginandregistration.controller;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import info.androidhive.loginandregistration.model.Group;
import info.androidhive.loginandregistration.model.Tupla;

public class GroupCommunication extends Observable {
    public static final String CREATE_GROUP_OK = "CREATE_GROUP_OK";
    public static final String CREATE_GROUP_ERROR = "CREATE_GROUP_ERROR" ;
    public static final String GET_USER_GROUPS_OK = "GET_USER_GROUPS_OK";
    public static final String GET_USER_GROUPS_ERROR = "GET_USER_GROUPS_ERROR";

    public static String URL_CREATE_GROUP = "http://34.69.44.48/instantm/crear_grupo.php";
    public static String URL_GET_GROUPS = "http://34.69.44.48/instantm/obtener_grupos.php";


    public void crateGroup(final String groupName, final String username, final String description) {
        CreateGroupListener createGroupListener = new CreateGroupListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_CREATE_GROUP, createGroupListener, createGroupListener) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", groupName);
                params.put("username", username);
                params.put("description", description);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    public void getUserGroups(final String username) {
        GetUserGroupsListener getUserGroupsListener = new GetUserGroupsListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_GET_GROUPS, getUserGroupsListener, getUserGroupsListener) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    class CreateGroupListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    setChanged();
                    notifyObservers(new Tupla<>(CREATE_GROUP_OK,null));
                } else {
                    setChanged();
                    notifyObservers(new Tupla<>(CREATE_GROUP_ERROR,"ERROR"));
                }
            } catch (JSONException e) {
                setChanged();
                notifyObservers(new Tupla<>(CREATE_GROUP_ERROR,"ERROR"));
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(CREATE_GROUP_ERROR,"ERROR"));
        }
    }

    class GetUserGroupsListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {

                    List<Group> groups = Group.JSONToGroups(jObj.getJSONArray("groups"));
                    setChanged();
                    notifyObservers(new Tupla<>(GET_USER_GROUPS_OK, groups));

                } else {
                    setChanged();
                    notifyObservers(new Tupla<>(GET_USER_GROUPS_ERROR, "ERROR"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(GET_USER_GROUPS_ERROR, "JSON ERROR"));
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(GET_USER_GROUPS_ERROR, "RESPONSE ERROR"));
        }
    }
}
