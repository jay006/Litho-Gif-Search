package com.example.lithogif.components;


import com.bumptech.glide.RequestManager;
import com.example.lithogif.R;
import com.example.lithogif.events.FavChangeEvent;
import com.example.lithogif.events.LikeChangeEvent;
import com.example.lithogif.models.GifItem;
import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.StateValue;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateInitialState;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.State;
import com.facebook.litho.widget.Image;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaPositionType;

@LayoutSpec(events = { LikeChangeEvent.class })
public class GifItemViewSpec {

    @OnCreateInitialState
    static void createInitialState(ComponentContext c, StateValue<Boolean> isLiked, @Prop boolean initLiked) {
        isLiked.set(initLiked);
    }

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext context, @Prop GifItem gif,
                                          @Prop RequestManager glide, @State boolean isLiked) {

        return Column.create(context)
                .child(GifImageView.create(context)
                        .gif(gif)
                        .glide(glide)
                        .alignSelf(YogaAlign.CENTER)
                        .build())
                .child(Image.create(context)
                        .drawableRes(isLiked ? R.drawable.ic_favorite_accent_24dp :R.drawable.ic_favorite_border_accent_24dp)
                        .clickHandler(GifItemView.onLikeButtonClicked(context))
                        .positionType(YogaPositionType.ABSOLUTE)
                        .widthDip(40)
                        .heightDip(40)
                        .paddingDip(YogaEdge.ALL, 8)
                        .alignSelf(YogaAlign.FLEX_END)
                        .build())
                .clickHandler(GifItemView.onViewClicked(context))
                .build();
    }

    @OnUpdateState
    static void updateLikeButton(StateValue<Boolean> isLiked, @Param boolean updatedValue) {
        isLiked.set(updatedValue);
    }

    @OnEvent(ClickEvent.class)
    static void onLikeButtonClicked(ComponentContext c, @State boolean isLiked, @Prop GifItem gif) {
        GifItemView.dispatchLikeChangeEvent(GifItemView.getLikeChangeEventHandler(c), !isLiked, gif.getId());
        GifItemView.updateLikeButtonAsync(c, !isLiked);
    }

    @OnEvent(ClickEvent.class)
    static void onViewClicked(ComponentContext c, @Prop GifItem gif, @Prop (optional = true) GifCallback callback) {
        if (callback != null) {
            callback.onGifSelected(gif, c.getComponentScope());
        }
    }

    @OnEvent(FavChangeEvent.class)
    static void onFavChanged(ComponentContext c, @FromEvent boolean isLiked, @FromEvent String gifId, @Prop GifItem gif) {
        if (gif.getId().equals(gifId)) {
            GifItemView.updateLikeButtonAsync(c, isLiked);
        }
    }

    public interface GifCallback {
        void onGifSelected(GifItem gif, Component gifComponent);
    }
}
