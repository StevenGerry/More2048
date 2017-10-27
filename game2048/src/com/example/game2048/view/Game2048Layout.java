//游戏界面的容器存放item方块
package com.example.game2048.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Game2048Layout extends RelativeLayout
{

//设定item数量即为行列数
	private int mColumn = 4;
//用来存放item
	private Game2048Item[] mGame2048Items;
//item的外边距
	private int mMargin = 10;
//item的内边距
	private int mPadding;
//监测用户操作手势
	private GestureDetector mGestureDetector;
//判断是否生成或改变新值的变量
	private boolean isMergeHappen = true;
	private boolean isMoveHappen = true;
//分数记录
	private int mScore;

	
	public interface OnGame2048Listener
	{
		void onScoreChange(int score);

		void onGameOver();
	}

	private OnGame2048Listener mGame2048Listener;

	public void setOnGame2048Listener(OnGame2048Listener mGame2048Listener)
	{
		this.mGame2048Listener = mGame2048Listener;
	}

//action作为运动方向的枚举	
	private enum ACTION
	{
		LEFT, RIGHT, UP, DOWM
	}

	public Game2048Layout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
//通过构造方法得到内边距和外边距
		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				mMargin, getResources().getDisplayMetrics());
		//为了四边一致，设定内边距为四边当中最小的一边
		mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
				getPaddingBottom());
//初始化手势探测
		mGestureDetector = new GestureDetector(context , new MyGestureDetector());
		Toast.makeText(getContext(), Integer.toString(mMargin)+" "+ Integer.toString(mPadding),Toast.LENGTH_SHORT).show();
	}

//程序核心算法
	private void action(ACTION action)
	{
		// 行列
		for (int i = 0; i < mColumn; i++)
		{
			List<Game2048Item> row = new ArrayList<Game2048Item>();
			// 行列
			for (int j = 0; j < mColumn; j++)
			{
				// 得到下标
				int index = getIndexByAction(action, i, j);
	//			Toast.makeText(getContext(), Integer.toString(index),Toast.LENGTH_SHORT).show();
				Game2048Item item = mGame2048Items[index];
				// 记录不为0的数
				if (item.getNumber() != 0)
				{
					row.add(item);
				}
			}
			
			//判断是否发生移动
			for (int j = 0; j < mColumn && j < row.size(); j++)
			{
				int index = getIndexByAction(action, i, j);
				Game2048Item item = mGame2048Items[index];

				if (item.getNumber() != row.get(j).getNumber())
				{
					isMoveHappen = true;
				}
			}
			
			// 合并相同的
			mergeItem(row);
			
			// 设置合并后的数值
			for (int j = 0; j < mColumn; j++)
			{
				int index = getIndexByAction(action, i, j);
				if (row.size() > j)
				{
					mGame2048Items[index].setNumber(row.get(j).getNumber());
				} else
				{
					mGame2048Items[index].setNumber(0);
				}
			}

		}
		//生成数字
		generateNum();

	}

//根据不同的手势按不同的顺序获取行或列的不为0的item
	private int getIndexByAction(ACTION action, int i, int j)
	{
		int index = -1;
		switch (action)
		{
		case LEFT:
			index = i * mColumn + j;
			break;
		case RIGHT:
			index = i * mColumn + mColumn - j - 1;
			break;
		case UP:
			index = i + j * mColumn;
			break;
		case DOWM:
			index = i + (mColumn - 1 - j) * mColumn;
			break;
		}
		return index;
	}


	private void mergeItem(List<Game2048Item> row)
	{
		//Toast.makeText(getContext(), Boolean.toString(isMoveHappen),Toast.LENGTH_SHORT).show();
		if (row.size() < 2)
			return;
		//遍历搜索
		for (int j = 0; j < row.size() - 1; j++)
		{
			Game2048Item item1 = row.get(j);
			Game2048Item item2 = row.get(j + 1);
			//遍历搜索查看发现有没有存在最靠前的两个相等的数字
			if (item1.getNumber() == item2.getNumber())
			{
				isMergeHappen = true;
				//如果发现两个相等的数字并且刚才位移已经发生，则进行两数字的相加
				int val = item1.getNumber() + item2.getNumber();
				//设定数字
				item1.setNumber(val);

				//游戏获得分数的加分，回调分数
				mScore += val;
				if (mGame2048Listener != null)
				{
					mGame2048Listener.onScoreChange(mScore);
				}

				//向前位移数字
				for (int k = j + 1; k < row.size() - 1; k++)
				{
					row.get(k).setNumber(row.get(k + 1).getNumber());
				}
				
				row.get(row.size() - 1).setNumber(0);
				return;
			}

		}
		
	}

//求多个不定值的最小值
	private int min(int... params)
	{
		int min = params[0];
		for (int param : params)
		{
			if (min > param)
			{
				min = param;
			}
		}
		return min;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	public Game2048Layout(Context context)
	{
		this(context, null);
	}

	public Game2048Layout(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	private boolean once;

//画界面	
//测量Layout的宽和高，以及设置Item的宽和高，这里忽略wrap_content 以宽、高之中的最小值绘制正方形
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获得正方形边长
		int length = Math.min(getMeasuredHeight(), getMeasuredWidth());
		// 获得item的宽度=childWidth
		int childWidth = (length - mPadding * 2 - mMargin * (mColumn - 1))
				/ mColumn;
		if (!once)
		{
			if (mGame2048Items == null)
			{
				mGame2048Items = new Game2048Item[mColumn * mColumn];
			}
			//放置Item
			for (int i = 0; i < mGame2048Items.length; i++)
			{
				Game2048Item item = new Game2048Item(getContext());

				mGame2048Items[i] = item;
				item.setId(i + 1);
				RelativeLayout.LayoutParams lp = new LayoutParams(childWidth,
				childWidth);
				//设置横向边距，不是最后一列
				if ((i + 1) % mColumn != 0)
				{
					lp.rightMargin = mMargin;
				}
				//如果不是第一列
				if (i % mColumn != 0)
				{
					//则将起放在上一个item的右侧
					lp.addRule(RelativeLayout.RIGHT_OF,//
							mGame2048Items[i - 1].getId());
				}
				//如果不是第一行
				if ((i + 1) > mColumn)
				{
					//设置纵向边距，非最后一行  
					lp.topMargin = mMargin;
					lp.addRule(RelativeLayout.BELOW,//
							mGame2048Items[i - mColumn].getId());
				}
				addView(item, lp);
			}
			generateNum();
		}
		once = true;

		setMeasuredDimension(length, length);
	}

 //判断是否满了
	private boolean isFull()
	{
		for (int i = 0; i < mGame2048Items.length; i++)
		{
			if (mGame2048Items[i].getNumber() == 0)
			{
				return false;
			}
		}
		return true;
	}

// 检测当前所有的位置都有数字，且相邻的没有相同的数字
	private boolean checkOver()
	{
		// 测是否所有位置都有数字
		if (!isFull())
		{
			return false;
		}

		for (int i = 0; i < mColumn; i++)
		{
			for (int j = 0; j < mColumn; j++)
			{

				int index = i * mColumn + j;

				// 当前Item
				Game2048Item item = mGame2048Items[index];
				// 右
				if ((index + 1) % mColumn != 0)
				{
					Log.e("TAG", "RIGHT");
					// 右边的Item
					Game2048Item itemRight = mGame2048Items[index + 1];
					if (item.getNumber() == itemRight.getNumber())
						return false;
				}
				// 下
				if ((index + mColumn) < mColumn * mColumn)
				{
					Log.e("TAG", "DOWN");
					Game2048Item itemBottom = mGame2048Items[index + mColumn];
					if (item.getNumber() == itemBottom.getNumber())
						return false;
				}
				// 左
				if (index % mColumn != 0)
				{
					Log.e("TAG", "LEFT");
					Game2048Item itemLeft = mGame2048Items[index - 1];
					if (itemLeft.getNumber() == item.getNumber())
						return false;
				}
				// 上
				if (index + 1 > mColumn)
				{
					Log.e("TAG", "UP");
					Game2048Item itemTop = mGame2048Items[index - mColumn];
					if (item.getNumber() == itemTop.getNumber())
						return false;
				}

			}

		}

		return true;

	}

	//产生数字的方法
	public void generateNum()
	{

		if (checkOver())
		{
			Log.e("TAG", "GAME OVER");
			if (mGame2048Listener != null)
			{
				mGame2048Listener.onGameOver();
			}
			return;
		}

		if (!isFull())
		{
			if (isMoveHappen || isMergeHappen)
			{
				Random random = new Random();
				int next = random.nextInt(16);
				Game2048Item item = mGame2048Items[next];

				while (item.getNumber() != 0)
				{
					next = random.nextInt(mColumn*mColumn);
					item = mGame2048Items[next];
				}

				item.setNumber(Math.random() > 0.75 ? 4 : 2);

				isMergeHappen = isMoveHappen = false;
			}

		}
	}

//重新开始游戏
	public void restart()
	{
		for (Game2048Item item : mGame2048Items)
		{
			item.setNumber(0);
		}
		mScore = 0;
		if (mGame2048Listener != null)
		{
			mGame2048Listener.onScoreChange(mScore);
		}
		isMoveHappen = isMergeHappen = true;
		generateNum();
	}

	//手势监听，并调用action进行移动
	class MyGestureDetector extends GestureDetector.SimpleOnGestureListener
	{

		final int FLING_MIN_DISTANCE = 50;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY)
		{
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();

			if (x > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > Math.abs(velocityY))
			{
				action(ACTION.RIGHT);
				//Toast.makeText(getContext(), "MoveRight",Toast.LENGTH_SHORT).show();

			} else if (x < -FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > Math.abs(velocityY))
			{
				action(ACTION.LEFT);
				//Toast.makeText(getContext(), "MoveLeft",Toast.LENGTH_SHORT).show();

			} else if (y > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) < Math.abs(velocityY))
			{
				action(ACTION.DOWM);
				//Toast.makeText(getContext(), "MoveDown",Toast.LENGTH_SHORT).show();

			} else if (y < -FLING_MIN_DISTANCE
					&& Math.abs(velocityX) < Math.abs(velocityY))
			{
				action(ACTION.UP);
				//Toast.makeText(getContext(), "MoveUp",Toast.LENGTH_SHORT).show();
			}
			return true;

		}

	}

}
