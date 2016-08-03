package com.codepath.snapmap.models;

import android.os.Build;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mbakhiet on 7/8/16.
 */


@ParseClassName("Post")
public class Post extends ParseObject {

    public Post() {
        super();
    }

    public String getId() {
        return getObjectId();
    }

    public void setId(String id) {
        setObjectId(id);
    }

    public void initializeLikes() {
        put("likedBy", new ArrayList<String>());
    }

    public void initializeChallenges() { put("challenge1", new JSONArray()); }

    public void initializeCoordinates() { put("coordinates", new ArrayList<Double>(2));}

    public JSONArray getLikedBy(){
        return getJSONArray("likedBy");
    }

    public JSONArray getChallengedBy() {return getJSONArray("challengeAcceptedBy"); }
    public void setChallenged(JSONArray array) {
        this.put("challengeAcceptedBy", array);
    }

    public String getDescription() {
        return getString("description");
    }
    public void setDescription(String description) {
        put("description",description);
    }

    public boolean like(String id) throws JSONException {

        boolean liked = true;
        JSONArray array = getJSONArray("likedBy");

        if (array != null){
            for (int i = 0; i < array.length(); i++){
                if (array.get(i).equals(id)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        array.remove(i);
                        liked = false;
                        //TODO: remove like from builds lower than 19
                    }
                }
            }
        }

        if (liked){
            this.addUnique("likedBy", id);
        }
        else{
            ArrayList<String> newArray = new ArrayList<String>();
            newArray.add(id);
            this.removeAll("likedBy", newArray);
        }

        this.saveInBackground();

        return liked;
    }

    public void setLikes(JSONArray array){
        this.put("likedBy", array);
    }

    public boolean challenged(String id) throws JSONException {

        boolean challenged = true;
        JSONArray array = getJSONArray("challengeAcceptedBy");

        if (array != null){
            for (int i = 0; i < array.length(); i++){
                if (array.get(i).equals(id)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        array.remove(i);
                        challenged = false;
                        //TODO: remove like from builds lower than 19
                    }
                }
            }
        }

        if (challenged){
            this.addUnique("challengeAcceptedBy", id);
        }
        else{
            ArrayList<String> newArray = new ArrayList<String>();
            newArray.add(id);
            this.removeAll("challengeAcceptedBy", newArray);
        }

        this.saveInBackground();

        return challenged;
    }

    public int getLikeCount(){

        JSONArray array = getJSONArray("likedBy");

        if (array != null){
            int likeCount = array.length();
            Log.d("DEBUG_LIKES", String.valueOf(array.length()));
            return likeCount;
        }

        return 0;
    }


    public int getChallengedCount(){

        JSONArray array = getJSONArray("challengeAcceptedBy");

        if (array != null){

            return array.length();
        }

        return 0;
    }

    public String getLocation() {
        return getString("location");
    }

    public void setLocation(String location) {
        put("location",location);
    }

    public JSONArray getChallenge() { return getJSONArray("challenge"); }
    public JSONArray getCoordinates() {
        return getJSONArray("coordinates");
    }

    public void setCoordinates(double latitude,double longitude) throws JSONException {
        JSONArray array = getJSONArray("coordinates");


        this.addUnique("coordinates",latitude);
        this.addUnique("coordinates",longitude);

        this.saveInBackground();
    }

    public void setChallenge(JSONArray challenge) {
        put("challenge",challenge);
    }




//    public String getChallenge() { return getString("challenge"); }
//
//    public void setChallenge(String challenge) { put("challenge", challenge); }


    public int getCount() {
        return getInt("upvoteCount");
    }

    public void increment(String count) {
        increment(count);
    }

    public void setPrivacy(String privacy){
        put("privacy", privacy);
    }

    public void setCategory(String category){
        put("category", category);
    }

    public void setPhotoFile(ParseFile file) { put("file", file); }

    public void setOwnerId(String id){
        put("parentId", id);
    }

    public void setOwner(ParseUser owner){
        put("owner", owner);
    }

    public void setCreationDate(Date day) { put("creationDate", day); }

    public ParseUser getOwner(){
        return getParseUser("owner");
    }

    public Date getCreationDate() {
        return getDate("creationDate");
    }

    public String getPrivacy(){
        return getString("privacy");
    }

    public String getCategory(){
        return getString("category");
    }

    public String getDay(){
        return getString("day");
    }

    public String getOwnerId() { return getString("parentId"); }

    public ParseFile getPhotoFile() { return getParseFile("file"); }

    public String getAction() { return getString("action"); }

    public void setAction(String action) { put("action",action); }

}