package com.example.lithogif.models.api;


import androidx.annotation.NonNull;

import com.example.lithogif.models.GifItem;
import com.example.lithogif.models.db.LikeStore;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GifProvider {

	private GifApi api;
	private final ResponseListener listener;
	private final LikeStore likeStore;

	public GifProvider(ResponseListener listener, LikeStore store) {
		this.listener = listener;
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(GifApi.ENDPOINT)
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		api = retrofit.create(GifApi.class);
		this.likeStore = store;
	}

	public void search(@NonNull String query) {
		Call<JsonObject> call = api.search(query, GifApi.API_KEY);
		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				JsonArray data = response.body().get("data").getAsJsonArray();
				List<GifItem> gifs = new ArrayList<>();
				for (int i=0; i<data.size(); i++) {
					JsonObject json = data.get(i).getAsJsonObject();
					gifs.add(new GifItem(json, likeStore.isLiked(json.get("id").getAsString())));
				}

				listener.onSuccess(gifs);
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				listener.onFailure(t);
			}
		});
	}

	public interface ResponseListener {
		void onSuccess(List<GifItem> gifs);
		void onFailure(Throwable t);
	}
}
