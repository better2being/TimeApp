package com.tsunami.timeapp.adpater;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;


import com.tsunami.timeapp.R;
import com.tsunami.timeapp.adpater.base.BaseRecyclerViewAdapter;
import com.tsunami.timeapp.model.SNote;
import com.tsunami.timeapp.utils.TimeUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jilihao
 */
public class NotesAdapter extends BaseRecyclerViewAdapter<SNote> implements Filterable{

    private final List<SNote> originalList;
    private Context mContext;
    public NotesAdapter(List<SNote> list) {
        super(list);
        originalList = new ArrayList<>(list);
    }

    public NotesAdapter(List<SNote> list, Context context) {
        super(list, context);
        originalList = new ArrayList<>(list);
    }

    // 2 在创建单个Item视图的ViewHolder时，先使用LayoutInflater填充出一个view，再通过NotesItemViewHolder包装获得ViewHolder。
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        final View view = LayoutInflater.from(mContext).inflate(R.layout.notes_item_layout, parent, false);
        return new NotesItemViewHolder(view);
    }

    // 2 绑定ViewHolder,此方法主要对ViewHolder中的控件进行赋值，在加载每个子项时调用此方法。
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        NotesItemViewHolder holder = (NotesItemViewHolder) viewHolder;
        SNote note = list.get(position);
        if (note == null)
            return;
        //TODO
        String label = "";
        if (mContext != null) {
            boolean b  = TextUtils.equals(mContext.getString(R.string.default_label), note.getLabel());
            label = b? "": note.getLabel();
        }
        holder.setLabelText(label);
        holder.setContentText(note.getContent());
        holder.setTimeText(TimeUtils.getConciseTime(note.getLastOprTime(), mContext));
        animate(viewHolder, position);
    }

    @Override
    public Filter getFilter() {
        return new NoteFilter(this, originalList);
    }

    // 2 笔记布局动画效果
    @Override
    protected Animator[] getAnimators(View view) {
        if (view.getMeasuredHeight() <=0){
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1.0f);
            return new ObjectAnimator[]{scaleX, scaleY};
        }
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1.0f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1.0f),
        };
    }

    @Override
    public void setList(List<SNote> list) {
        super.setList(list);
        this.originalList.clear();
        originalList.addAll(list);
    }


    // 2 此类主要由搜索功能调用，构造函数对originalList进行赋值，
    // performFiltering(…)方法进行过滤操作，
    // 过滤后列表存入filteredList，并且返回FilterResults以便后用，
    // publishResults(…)方法进行展示filteredList的内容
    private static class NoteFilter extends Filter{

        private final NotesAdapter adapter;

        private final List<SNote> originalList;

        private final List<SNote> filteredList;

        private NoteFilter(NotesAdapter adapter, List<SNote> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<>(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                for ( SNote note : originalList) {
                    if (note.getContent().contains(constraint) || note.getLabel().contains(constraint)) {
                        filteredList.add(note);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.list.clear();
            adapter.list.addAll((ArrayList<SNote>) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}
