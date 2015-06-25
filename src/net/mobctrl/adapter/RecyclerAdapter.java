package net.mobctrl.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.mobctrl.interfaces.ItemDataClickListener;
import net.mobctrl.interfaces.OnScrollToListener;
import net.mobctrl.model.ItemData;
import net.mobctrl.treerecyclerview.R;
import net.mobctrl.viewholder.BaseViewHolder;
import net.mobctrl.viewholder.ChildViewHolder;
import net.mobctrl.viewholder.ParentViewHolder;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class RecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

	private Context mContext;
	private List<ItemData> mDataSet;
	private OnScrollToListener onScrollToListener;

	public void setOnScrollToListener(OnScrollToListener onScrollToListener) {
		this.onScrollToListener = onScrollToListener;
	}

	public RecyclerAdapter(Context context) {
		mContext = context;
		mDataSet = new ArrayList<ItemData>();
	}

	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		switch (viewType) {
		case ItemData.ITEM_TYPE_PARENT:
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_recycler_parent, parent, false);
			return new ParentViewHolder(view);
		case ItemData.ITEM_TYPE_CHILD:
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_recycler_child, parent, false);
			return new ChildViewHolder(view);
		default:
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_recycler_parent, parent, false);
			return new ChildViewHolder(view);
		}
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position) {
		switch (getItemViewType(position)) {
		case ItemData.ITEM_TYPE_PARENT:
			ParentViewHolder imageViewHolder = (ParentViewHolder) holder;
			imageViewHolder.bindView(mDataSet.get(position), position,
					imageClickListener);
			break;
		case ItemData.ITEM_TYPE_CHILD:
			ChildViewHolder textViewHolder = (ChildViewHolder) holder;
			textViewHolder.bindView(mDataSet.get(position), position);
			break;
		default:
			break;
		}
	}

	private ItemDataClickListener imageClickListener = new ItemDataClickListener() {

		@Override
		public void onExpandChildren(ItemData itemData) {
			int position = getCurrentPosition(itemData.getUuid());
			List<ItemData> children = getChildrenByPath(itemData.getPath(),
					itemData.getTreeDepth());
			if (children == null) {
				return;
			}
			addAll(children, position + 1);// 插入到点击点的下方
			itemData.setChildren(children);
			if (onScrollToListener != null) {
				onScrollToListener.scrollTo(position + 1);
			}
		}

		@Override
		public void onHideChildren(ItemData itemData) {
			int position = getCurrentPosition(itemData.getUuid());
			List<ItemData> children = itemData.getChildren();
			if (children == null) {
				return;
			}
			removeAll(position + 1, getChildrenCount(itemData) - 1);
			if (onScrollToListener != null) {
				onScrollToListener.scrollTo(position);
			}
			itemData.setChildren(null);
		}
	};

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}

	private int getChildrenCount(ItemData item) {
		List<ItemData> list = new ArrayList<ItemData>();
		printChild(item, list);
		return list.size();
	}

	private void printChild(ItemData item, List<ItemData> list) {
		list.add(item);
		if (item.getChildren() != null) {
			for (int i = 0; i < item.getChildren().size(); i++) {
				printChild(item.getChildren().get(i), list);
			}
		}
	}

	/**
	 * 根据路径获取子目录或文件
	 * 
	 * @param path
	 * @param treeDepth
	 * @return
	 */
	public List<ItemData> getChildrenByPath(String path, int treeDepth) {
		treeDepth++;
		try {
			List<ItemData> list = new ArrayList<ItemData>();
			File file = new File(path);
			File[] children = file.listFiles();
			List<ItemData> fileList = new ArrayList<ItemData>();
			for (File child : children) {
				if (child.isDirectory()) {
					list.add(new ItemData(ItemData.ITEM_TYPE_PARENT, child
							.getName(), child.getAbsolutePath(), UUID
							.randomUUID().toString(), treeDepth, null));
				} else {
					fileList.add(new ItemData(ItemData.ITEM_TYPE_CHILD, child
							.getName(), child.getAbsolutePath(), UUID
							.randomUUID().toString(), treeDepth, null));
				}
			}
			Collections.sort(list);
			Collections.sort(fileList);
			list.addAll(fileList);
			return list;
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * 从position开始删除，删除
	 * 
	 * @param position
	 * @param itemCount
	 *            删除的数目
	 */
	protected void removeAll(int position, int itemCount) {
		for (int i = 0; i < itemCount; i++) {
			mDataSet.remove(position);
		}
		notifyItemRangeRemoved(position, itemCount);
	}

	protected int getCurrentPosition(String uuid) {
		for (int i = 0; i < mDataSet.size(); i++) {
			if (uuid.equalsIgnoreCase(mDataSet.get(i).getUuid())) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getItemViewType(int position) {
		return mDataSet.get(position).getType();
	}

	public void add(ItemData text, int position) {
		mDataSet.add(position, text);
		notifyItemInserted(position);
	}

	public void addAll(List<ItemData> list, int position) {
		mDataSet.addAll(position, list);
		notifyItemRangeInserted(position, list.size());
	}

	public void delete(int pos) {
		if (pos >= 0 && pos < mDataSet.size()) {
			if (mDataSet.get(pos).getType() == ItemData.ITEM_TYPE_PARENT
					&& mDataSet.get(pos).isExpand()) {// 父组件并且子节点已经展开
				for (int i = 0; i < mDataSet.get(pos).getChildren().size() + 1; i++) {
					mDataSet.remove(pos);
				}
				notifyItemRangeRemoved(pos, mDataSet.get(pos).getChildren()
						.size() + 1);
			} else {// 孩子节点，或没有展开的父节点
				mDataSet.remove(pos);
				notifyItemRemoved(pos);
			}
		}
	}
}
