package net.mobctrl.viewholder;

import java.io.File;

import net.mobctrl.model.ItemData;
import net.mobctrl.treerecyclerview.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class ChildViewHolder extends BaseViewHolder {

	public TextView text;
	public ImageView image;
	public RelativeLayout relativeLayout;
	private int itemMargin;
	private int offsetMargin;

	public ChildViewHolder(View itemView) {
		super(itemView);
		text = (TextView) itemView.findViewById(R.id.text);
		image = (ImageView) itemView.findViewById(R.id.image);
		relativeLayout = (RelativeLayout) itemView.findViewById(R.id.container);
		itemMargin = itemView.getContext().getResources()
				.getDimensionPixelSize(R.dimen.item_margin);
		offsetMargin = itemView.getContext().getResources()
				.getDimensionPixelSize(R.dimen.expand_size);
	}

	public void bindView(final ItemData itemData, int position) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image
				.getLayoutParams();
		params.leftMargin = itemMargin * itemData.getTreeDepth() + offsetMargin;
		image.setLayoutParams(params);
		text.setText(itemData.getText());
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				openFileInSystem(itemData.getPath(), view.getContext());
			}
		});
	}

	private void openFileInSystem(String path, Context context) {
		try {
			MimeTypeMap myMime = MimeTypeMap.getSingleton();
			Intent newIntent = new Intent(Intent.ACTION_VIEW);
			String mimeType = myMime.getMimeTypeFromExtension(fileExt(path)
					.substring(1));
			newIntent.setDataAndType(Uri.fromFile(new File(path)), mimeType);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(newIntent);
		} catch (Exception e) {
			Toast.makeText(context, "No handler for this type of file.",
					Toast.LENGTH_LONG).show();
		}
	}

	@SuppressLint("DefaultLocale")
	private String fileExt(String url) {
		if (url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		if (url.lastIndexOf(".") == -1) {
			return null;
		} else {
			String ext = url.substring(url.lastIndexOf("."));
			if (ext.indexOf("%") > -1) {
				ext = ext.substring(0, ext.indexOf("%"));
			}
			if (ext.indexOf("/") > -1) {
				ext = ext.substring(0, ext.indexOf("/"));
			}
			return ext.toLowerCase();
		}
	}

}
