package net.mobctrl.viewholder;

import java.util.List;

import net.mobctrl.interfaces.ItemDataClickListener;
import net.mobctrl.model.ItemData;
import net.mobctrl.treerecyclerview.R;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class ParentViewHolder extends BaseViewHolder {

	public ImageView image;
	public TextView text;
	public ImageView expand;
	public TextView count;
	public RelativeLayout relativeLayout;
	private int itemMargin;

	public ParentViewHolder(View itemView) {
		super(itemView);
		image = (ImageView) itemView.findViewById(R.id.image);
		text = (TextView) itemView.findViewById(R.id.text);
		expand = (ImageView) itemView.findViewById(R.id.expand);
		count = (TextView) itemView.findViewById(R.id.count);
		relativeLayout = (RelativeLayout) itemView.findViewById(R.id.container);
		itemMargin = itemView.getContext().getResources()
				.getDimensionPixelSize(R.dimen.item_margin);
	}

	public void bindView(final ItemData itemData, final int position,
			final ItemDataClickListener imageClickListener) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) expand
				.getLayoutParams();
		params.leftMargin = itemMargin * itemData.getTreeDepth();
		expand.setLayoutParams(params);
		text.setText(itemData.getText());
		if (itemData.isExpand()) {
			expand.setRotation(45);
			List<ItemData> children = itemData.getChildren();
			if (children != null) {
				count.setText(String.format("(%s)", itemData.getChildren()
						.size()));
			 count.setVisibility(View.VISIBLE);
						} else {
                            count.setVisibility(View.GONE);
                        }
		} else {
			expand.setRotation(0);
			count.setVisibility(View.GONE);
		}
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imageClickListener != null) {
					if (itemData.isExpand()) {
						imageClickListener.onHideChildren(itemData);
						itemData.setExpand(false);
						rotationExpandIcon(45, 0);
						count.setVisibility(View.GONE);
					} else {
						imageClickListener.onExpandChildren(itemData);
						itemData.setExpand(true);
						rotationExpandIcon(0, 45);
						List<ItemData> children = itemData.getChildren();
						if (children != null) {
							count.setText(String.format("(%s)", itemData
									.getChildren().size()));
						}
						count.setVisibility(View.VISIBLE);
					}
				}

			}
		});
		image.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				Toast.makeText(view.getContext(), "longclick",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void rotationExpandIcon(float from, float to) {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
			valueAnimator.setDuration(150);
			valueAnimator.setInterpolator(new DecelerateInterpolator());
			valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					expand.setRotation((Float) valueAnimator.getAnimatedValue());
				}
			});
			valueAnimator.start();
		}
	}
}
