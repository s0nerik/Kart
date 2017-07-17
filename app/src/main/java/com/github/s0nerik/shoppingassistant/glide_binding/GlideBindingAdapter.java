package com.github.s0nerik.shoppingassistant.glide_binding;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;

public class GlideBindingAdapter {
    private static <T> DrawableTypeRequest<?> getDrawableRequest(ImageView iv, T oldPath, T newPath) {
        if (newPath != null && !newPath.equals(oldPath)) {
            return Glide.with(iv.getContext()).load(newPath);
        } else {
            return null;
        }
    }

    private static <T> void loadImage(
            ImageView iv,
            T oldPath,
            T newPath,
            Object configProviderKey
    ) {
        DrawableTypeRequest<?> drawableRequest = getDrawableRequest(iv, oldPath, newPath);
        if (drawableRequest != null) {
            GlideBindingConfig.Provider configProvider;
            DrawableRequestBuilder<?> requestBuilder;
            if ((configProvider = GlideBindingConfig.getProvider(configProviderKey)) != null) {
                requestBuilder = configProvider.provide(iv, drawableRequest);
            } else if ((configProvider = GlideBindingConfig.getDefaultProvider()) != null) {
                requestBuilder = configProvider.provide(iv, drawableRequest);
            } else {
                requestBuilder = drawableRequest;
            }
            requestBuilder.into(iv);
        }
    }

    @BindingAdapter("glideSrc")
    public static void setGlideImagePath(ImageView iv, String oldPath, String newPath) {
        loadImage(iv, oldPath, newPath, null);
    }

    @BindingAdapter({"glideSrc", "glideConfig"})
    public static void setGlideImagePath(
            ImageView iv,
            String oldPath,
            Object oldGlideConfigProviderKey,
            String newPath,
            Object newGlideConfigProviderKey
    ) {
        loadImage(iv, oldPath, newPath, newGlideConfigProviderKey);
    }

    @BindingAdapter("glideSrc")
    public static void setGlideImageUri(ImageView iv, Uri oldUri, Uri newUri) {
        loadImage(iv, oldUri, newUri, null);
    }

    @BindingAdapter({"glideSrc", "glideConfig"})
    public static void setGlideImageUri(
            ImageView iv,
            Uri oldUri,
            Object oldGlideConfigProviderKey,
            Uri newUri,
            Object newGlideConfigProviderKey
    ) {
        loadImage(iv, oldUri, newUri, newGlideConfigProviderKey);
    }
}