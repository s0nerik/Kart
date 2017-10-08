package com.github.s0nerik.shoppingassistant.screens.product

class CreateProductAnimator(
    val activity: CreateProductActivity
) {
    fun appear() {
//        with (activity) {
//            val rightButtons = arrayOf(btnSelectName, btnSelectPrice, btnSelectCategory, btnSelectShop)
//            val leftButtons = arrayOf(btnFavorite)
//            val bottomButtons = arrayOf(btnCancelPurchase, btnPurchaseNewProduct)
//
//            val previewAppearDuration = 500L
//            val previewAppearDelay = 200L
//            val buttonsAppearOffset = previewAppearDuration + previewAppearDelay
//
//            root.alpha = 0f
//            root.animate()
//                .alpha(1f)
//                .setDuration(500L)
//                .setInterpolator(FastOutSlowInInterpolator())
//                .start()
//
//            preview.alpha = 0f
//            preview.translationY = dip(48).toFloat()
//            preview.animate()
//                .alpha(1f)
//                .translationY(0f)
//                .setDuration(previewAppearDuration)
//                .setStartDelay(previewAppearDelay)
//                .setInterpolator(FastOutSlowInInterpolator())
//                .start()
//
//            rightButtons.forEachIndexed { i, v ->
//                v.translationX = dip(-48).toFloat()
//                v.alpha = 0f
//                v.animate()
//                    .translationX(0f)
//                    .alpha(1f)
//                    .setDuration(200)
//                    .setStartDelay(buttonsAppearOffset + i * 50L)
//                    .start()
//            }
//
//            leftButtons.forEachIndexed { i, v ->
//                v.translationX = dip(48).toFloat()
//                v.alpha = 0f
//                v.animate()
//                    .translationX(0f)
//                    .alpha(1f)
//                    .setDuration(200)
//                    .setStartDelay(buttonsAppearOffset + i * 50L + rightButtons.size * 50L)
//                    .start()
//            }
//
//            bottomButtons.forEachIndexed { i, v ->
//                v.translationY = dip(-24).toFloat()
//                v.alpha = 0f
//                v.animate()
//                    .translationY(0f)
//                    .alpha(1f)
//                    .setDuration(200)
//                    .setStartDelay(buttonsAppearOffset + i * 50L + rightButtons.size * 50L + leftButtons.size * 50L)
//                    .start()
//            }
//        }
    }

    fun disappear(callback: () -> Unit) {
        callback()
//        with(activity) {
//            val animators = mutableListOf<Animator>()
//
//            val rightButtons = arrayOf(btnSelectName, btnSelectPrice, btnSelectCategory, btnSelectShop).reversed()
//            val leftButtons = arrayOf(btnFavorite).reversed()
//            val bottomButtons = arrayOf(btnCancelPurchase, btnPurchaseNewProduct).reversed()
//
//            val getDelay = { i: Int -> i * 50L }
//
//            bottomButtons.forEachIndexed { i, v ->
//                animators += ViewPropertyObjectAnimator
//                    .animate(v)
//                    .translationY(dip(-24).toFloat())
//                    .alpha(0f)
//                    .setDuration(200)
//                    .setStartDelay(getDelay(i))
//                    .get()
//            }
//
//            leftButtons.forEachIndexed { i, v ->
//                animators += ViewPropertyObjectAnimator
//                    .animate(v)
//                    .translationX(dip(48).toFloat())
//                    .alpha(0f)
//                    .setDuration(200)
//                    .setStartDelay(getDelay(i + bottomButtons.size))
//                    .get()
//            }
//
//            rightButtons.forEachIndexed { i, v ->
//                animators += ViewPropertyObjectAnimator
//                    .animate(v)
//                    .translationX(dip(-48).toFloat())
//                    .alpha(0f)
//                    .setDuration(200)
//                    .setStartDelay(getDelay(i + bottomButtons.size + leftButtons.size))
//                    .get()
//            }
//
//            animators += ViewPropertyObjectAnimator
//                .animate(preview)
//                .alpha(0f)
//                .translationY(dip(48).toFloat())
//                .setDuration(500L)
//                .setInterpolator(FastOutSlowInInterpolator())
//                .setStartDelay(getDelay(rightButtons.size + bottomButtons.size + leftButtons.size))
//                .get()
//
//            animators += ViewPropertyObjectAnimator
//                .animate(root)
//                .alpha(0f)
//                .setDuration(500L)
//                .setInterpolator(FastOutSlowInInterpolator())
//                .setStartDelay(getDelay(rightButtons.size + bottomButtons.size + leftButtons.size) + 200L)
//                .get()
//
//            val set = AnimatorSet()
//            set.addListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) = callback()
//                override fun onAnimationCancel(animation: Animator?) = callback()
//            })
//            set.playTogether(animators)
//            set.start()
//        }
    }
}