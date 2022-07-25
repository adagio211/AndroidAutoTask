package com.adagio.autotask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.adagio.autotask.R;
import com.adagio.autotask.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    private class Circle extends View {
        Paint paint = new Paint();
        float x, y;

        public Circle(Context context, float x, float y) {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            this.setX(x);
            this.setY(y);
            this.bringToFront();

            canvas.drawColor(Color.WHITE);

            paint.setAntiAlias(true);//抗锯齿
            paint.setColor(Color.RED);//画笔颜色
            paint.setStyle(Paint.Style.STROKE);//描边模式
            paint.setStrokeWidth(4f);//设置画笔粗细度
            paint.setStyle(Paint.Style.FILL);//填充模式
            paint.setColor(Color.BLUE);//画笔颜色

            canvas.drawCircle(x, y, 50f, paint);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        new Circle(getContext(), 200, 200);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        binding.clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("E", "Click");
                new Circle(getContext(), 200, 200);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    IntStream.of(100).forEach(c -> {

//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
                                final float x = (float) (Math.random() * 600 + 200);
                                final float y = (float) (Math.random() * 1000 + 100);

//                                try {
//                                    execCommand("input keyevent KEYCODE_HOME");
//                                } catch (Throwable e) {
//                                    Log.e("ERROR", e.getMessage(), e);
//                                }

//                                Runtime.getRuntime().exec("input tab 300 300 \n");

                                binding.stop.setX(x);
                                binding.stop.setY(y);

                                long downTime = SystemClock.uptimeMillis();
                                final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                                        MotionEvent.ACTION_DOWN, x, y, 0);
                                downTime += 1000;
                                final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                                        MotionEvent.ACTION_UP, x, y, 0);
                                view.onTouchEvent(downEvent);
                                view.onTouchEvent(upEvent);
                                downEvent.recycle();
                                upEvent.recycle();
                                Toast.makeText(getContext(), "Click x=" + x + ",y=" + y, Toast.LENGTH_LONG).show();
                            }
//                        }, 500);
////                    });
//                }
//            }
        });
    }

    private void execCommand(String command) {
        Process su = null;
        try {
            su = Runtime.getRuntime().exec("su");
            su.getOutputStream().write((command + "\n").getBytes());
            su.getOutputStream().write("exit\n".getBytes());
            su.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (su != null) {
                su.destroy();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}