package net.mobctrl.activity;

import java.util.List;

import net.mobctrl.adapter.RecyclerAdapter;
import net.mobctrl.interfaces.OnScrollToListener;
import net.mobctrl.model.ItemData;
import net.mobctrl.treerecyclerview.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class MainActivity extends Activity {

	private RecyclerView recyclerView;

	private RecyclerAdapter myAdapter;

	private LinearLayoutManager linearLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);

		recyclerView.getItemAnimator().setAddDuration(100);
		recyclerView.getItemAnimator().setRemoveDuration(100);
		recyclerView.getItemAnimator().setMoveDuration(200);
		recyclerView.getItemAnimator().setChangeDuration(100);

		myAdapter = new RecyclerAdapter(this);
		recyclerView.setAdapter(myAdapter);
		myAdapter.setOnScrollToListener(new OnScrollToListener() {

			@Override
			public void scrollTo(int position) {
				recyclerView.scrollToPosition(position);
			}
		});
		initDatas();
	}

	private void initDatas() {
		List<ItemData> list = myAdapter.getChildrenByPath(Environment
				.getExternalStorageDirectory().getPath(), 0);
		myAdapter.addAll(list, 0);
	}

}
