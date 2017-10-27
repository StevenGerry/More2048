package com.example.game2048.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

//1500867 build
public class Game2048Item extends View
{
//�趨Views������
	private int mNumber;
	private String mNumberVal;
	private Paint mPaint;//���ֻ���
	//��ȡ��Ļ��С
	WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	int width = wm.getDefaultDisplay().getWidth();
	int height = wm.getDefaultDisplay().getHeight();
//�������ֵ�����
	private Rect mBound;
	float RATIO = Math.min(height/800, width/480);
//�ع��췽��
	public Game2048Item(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mPaint = new Paint();
	}

	public Game2048Item(Context context)
	{
		this(context, null);
	}

	public Game2048Item(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
//�趨����
	DisplayMetrics displayMetrics = new DisplayMetrics();  

	
	public void setNumber(int number)
	{
		mNumber = number;
		mNumberVal = mNumber + "";
		//Toast.makeText(getContext(), Float.toString(RATIO),Toast.LENGTH_SHORT).show();
		mPaint.setTextSize(35*RATIO);//������Ļ��С���������С
		mBound = new Rect();//������Χ������
		mPaint.getTextBounds(mNumberVal, 0, mNumberVal.length(), mBound);//��ȷ�������ַ���
		invalidate();
	}
//ȡ��ĳһλ�õ����ַ���
	public int getNumber()
	{
		return mNumber;
	}
//ʵ��canvasͼ�η������������ֽ��л��Ʊ���ɫ
	@Override
	protected void onDraw(Canvas canvas)
	{
		
		super.onDraw(canvas);
		String mBgColor = "";
		switch (mNumber)
		{
		case 0:
			mBgColor = "#CCC0B3";
			break;
		case 2:
			mBgColor = "#EEE4DA";
			break;
		case 4:
			mBgColor = "#EDE0C8";
			break;
		case 8:
			mBgColor = "#F2B179";
			break;
		case 16:
			mBgColor = "#F49563";
			break;
		case 32:
			mBgColor = "#F5794D";
			break;
		case 64:
			mBgColor = "#F55D37";
			break;
		case 128:
			mBgColor = "#EEE863";
			break;
		case 256:
			mBgColor = "#EDB04D";
			break;
		case 512:
			mBgColor = "#ECB04D";
			break;
		case 1024:
			mBgColor = "#EB9437";
			break;
		case 2048:
			mBgColor = "#EA7821";
			break;
		case 4096:
			mBgColor = "#5f1460";
			break;
		default:
			mBgColor = "#EB9437";
			break;
		}

		mPaint.setColor(Color.parseColor(mBgColor));
		mPaint.setStyle(Style.FILL_AND_STROKE);//fix FILL
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);//ʹ��canvas����λ滭����
		//0,0Ϊ�����Ͻǿ�ʼ�����ұߺ��±߷ֱ�Ϊ���Ϳ�

		if (mNumber != 0)
			drawText(canvas);

	}

//��������
	private void drawText(Canvas canvas)
	{
		
		mPaint.setColor(Color.BLACK);//���ֻ��ʵ���ɫ
		float x = (getWidth() - mBound.width()) / 2;//���е�x
		float y = getHeight() / 2 + mBound.height() / 2;//���е�y
		canvas.drawText(mNumberVal, x, y, mPaint);//�������֣����֣����꣬���ʣ�
	}

}
