package hust.beamforce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    //静态成员变量，平面直梁
    public static Beam beam = new Beam();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //显示梁模型
        final LinearLayout wrap_draw_beam = (LinearLayout) findViewById(R.id.wrap_draw_beam);
        ViewTreeObserver observer = wrap_draw_beam.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int layout_width = wrap_draw_beam.getWidth();
                DrawBeam drawBeam = new DrawBeam(MainActivity.this, layout_width, 5);
                wrap_draw_beam.removeAllViews();
                wrap_draw_beam.addView(drawBeam);
                wrap_draw_beam.setTag(drawBeam);
                wrap_draw_beam.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        //界面中显示梁模型信息的TextView
        final TextView beamInfo = (TextView) findViewById(R.id.beam_info);

        //绘图按钮
        final Button drawButton = (Button) findViewById(R.id.draw_button);
        drawButton.setEnabled(false);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(beam.checkAll()){
                    Intent intent = new Intent(MainActivity.this, DrawActivity.class);
                    startActivity(intent);
                }
            }
        });

        //执行梁的求解器的按钮
        final Button solverButton = (Button) findViewById(R.id.solver_button);
        solverButton.setEnabled(false);
        solverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(beam.check()){
                    beam.constraintSolver();
                }
                refresh(wrap_draw_beam,beamInfo,solverButton, drawButton);
                drawButton.setText("绘图");
                drawButton.setBackgroundColor(0xffd50000);
                drawButton.setEnabled(true);
            }
        });

        //弹出输入梁的几何尺寸提示框的按钮
        Button geometryDialogButton = (Button) findViewById(R.id.geometry_Button);
        geometryDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将指定的xml布局文件化为View实例
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View input_geometry_view = inflater.inflate(R.layout.input_geometry, null);
                final EditText lengthText = (EditText) input_geometry_view.findViewById(R.id.length);
                if(beam.getState(0)){
                    lengthText.setText(beam.getLength().toString());
                }

                //构建提示框
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.input_geometry)
                        .setView(input_geometry_view)
                        .setCancelable(false)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                //显示提示框
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取输入内容并设定梁的参数

                        if(!TextUtils.isEmpty(lengthText.getText())) {
                            Double lengthValue = Double.parseDouble(lengthText.getText().toString());
                            if(beam.setLength(lengthValue)){
                                refresh(wrap_draw_beam,beamInfo,solverButton, drawButton);
                                dialog.dismiss();
                            } else {
                                lengthText.setError("请输入合适的长度");
                            }
                        } else {
                            lengthText.setError("请输入梁的长度");
                        }
                    }
                });
            }
        });

        //弹出输入材料性质提示框的按钮
        Button materialDialogButton = (Button) findViewById(R.id.material_Button);
        materialDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View input_material_view = inflater.inflate(R.layout.input_material, null);
                final EditText EText = (EditText) input_material_view.findViewById(R.id.E);
                final EditText IText = (EditText) input_material_view.findViewById(R.id.I);
                if(beam.getState(1)){
                    EText.setText(beam.getE().toString());
                    IText.setText(beam.getI().toString());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.input_material)
                        .setView(input_material_view)
                        .setCancelable(false)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if((!TextUtils.isEmpty(EText.getText()))&&(!TextUtils.isEmpty(IText.getText()))) {
                            Double E = Double.parseDouble(EText.getText().toString());
                            Double I = Double.parseDouble(IText.getText().toString());
                            if(beam.setEI(E,I)){
                                refresh(wrap_draw_beam,beamInfo,solverButton, drawButton);
                                dialog.dismiss();
                            } else {
                                if(E <= 0) EText.setError("请输入合适的参数");
                                if(I <= 0) IText.setError("请输入合适的参数");
                            }
                        } else {
                            if(TextUtils.isEmpty(EText.getText())){
                                EText.setError("请输入弹性模量");
                            }
                            if(TextUtils.isEmpty(IText.getText())){
                                IText.setError("请输入惯性矩");
                            }
                        }
                    }
                });
            }
        });

        //弹出选择约束类型提示框的按钮
        Button constraintDialogButton = (Button) findViewById(R.id.constraint_Button);
        constraintDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View input_constraint_view = inflater.inflate(R.layout.input_constraint, null);

                final TextView constraint_info = (TextView) input_constraint_view.findViewById(R.id.constraint_info);

                final Spinner left_constraint = (Spinner) input_constraint_view.findViewById(R.id.left_constraint);
                final Spinner right_constraint = (Spinner) input_constraint_view.findViewById(R.id.right_constraint);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                        R.array.constraint_items, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                left_constraint.setAdapter(adapter);
                right_constraint.setAdapter(adapter);
                if(beam.getState(2)){
                    left_constraint.setSelection(beam.getConstraintType()[0],true);
                    right_constraint.setSelection(beam.getConstraintType()[1],true);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.input_constraint)
                        .setView(input_constraint_view)
                        .setCancelable(false)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String left_constraint_text = left_constraint.getSelectedItem().toString();
                        String right_constraint_text = right_constraint.getSelectedItem().toString();

                        if(beam.setConstraint(left_constraint_text,right_constraint_text)) {
                            refresh(wrap_draw_beam,beamInfo,solverButton, drawButton);
                            dialog.dismiss();
                        } else {
                            constraint_info.setText("结构不稳定的约束类型");
                        }
                    }
                });
            }
        });

        //弹出输入载荷提示框的按钮
        Button loadsDialogButton = (Button) findViewById(R.id.loads_Button);
        loadsDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View select_loads_type_view = inflater.inflate(R.layout.select_load_type, null);

                Button type_1 = (Button) select_loads_type_view.findViewById(R.id.type_1);
                Button type_2 = (Button) select_loads_type_view.findViewById(R.id.type_2);
                Button type_3 = (Button) select_loads_type_view.findViewById(R.id.type_3);
                Button edit_load = (Button) select_loads_type_view.findViewById(R.id.edit_load);

                type_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        final View input_loads = inflater.inflate(R.layout.input_loads, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("请输入均布力")
                                .setView(input_loads)
                                .setCancelable(false)
                                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {}
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {}
                                });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText loadValue = (EditText) input_loads.findViewById(R.id.load);
                                EditText left_position = (EditText) input_loads.findViewById(R.id.left_position);
                                EditText right_position = (EditText) input_loads.findViewById(R.id.right_position);
                                if(TextUtils.isEmpty(loadValue.getText())){
                                    loadValue.setError("请输入载荷大小");
                                }
                                if(TextUtils.isEmpty(left_position.getText())){
                                    left_position.setError("请输入左端作用位置");
                                }
                                if(TextUtils.isEmpty(right_position.getText())){
                                    right_position.setError("请输入右端作用位置");
                                }
                                if((!TextUtils.isEmpty(loadValue.getText()))&&(!TextUtils.isEmpty(left_position.getText()))&&(!TextUtils.isEmpty(right_position.getText()))) {
                                    Double value = Double.parseDouble(loadValue.getText().toString());
                                    Double leftPosition = Double.parseDouble(left_position.getText().toString());
                                    Double rightPosition = Double.parseDouble(right_position.getText().toString());

                                    Load load = new Load(beam);
                                    load.setType("均布力");
                                    if(!load.setValue(value)){
                                        loadValue.setError("请输入合适的载荷大小");
                                    }
                                    if(!load.setPosition(leftPosition,rightPosition)){
                                        left_position.setError("请输入合适的作用位置");
                                        right_position.setError("请输入合适的作用位置");
                                    }
                                    if(load.setValue(value) && load.setPosition(leftPosition,rightPosition)){
                                        beam.addLoads(load);
                                        refresh(wrap_draw_beam,beamInfo,solverButton, drawButton);
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });
                    }
                });
                type_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        final View input_loads = inflater.inflate(R.layout.input_loads, null);

                        TableRow hide_row = (TableRow) input_loads.findViewById(R.id.hide_row);
                        hide_row.setVisibility(View.GONE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("请输入集中力")
                                .setView(input_loads)
                                .setCancelable(false)
                                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {}
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {}
                                });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText loadValue = (EditText) input_loads.findViewById(R.id.load);
                                EditText position = (EditText) input_loads.findViewById(R.id.left_position);

                                if(TextUtils.isEmpty(loadValue.getText())){
                                    loadValue.setError("请输入载荷大小");
                                }
                                if(TextUtils.isEmpty(position.getText())){
                                    position.setError("请输入作用位置");
                                }
                                if((!TextUtils.isEmpty(loadValue.getText()))&&(!TextUtils.isEmpty(position.getText()))) {
                                    Double value = Double.parseDouble(loadValue.getText().toString());
                                    Double Position = Double.parseDouble(position.getText().toString());

                                    Load load = new Load(beam);
                                    load.setType("集中力");
                                    if(!load.setValue(value)){
                                        loadValue.setError("请输入合适的载荷大小");
                                    }
                                    if(!load.setPosition(Position,Position)){
                                        position.setError("请输入合适的作用位置");
                                    }
                                    if(load.setValue(value) && load.setPosition(Position,Position)){
                                        beam.addLoads(load);
                                        refresh(wrap_draw_beam,beamInfo,solverButton, drawButton);
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });
                    }
                });
                type_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        final View input_loads = inflater.inflate(R.layout.input_loads, null);

                        TableRow hide_row = (TableRow) input_loads.findViewById(R.id.hide_row);
                        hide_row.setVisibility(View.GONE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("请输入弯矩")
                                .setView(input_loads)
                                .setCancelable(false)
                                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {}
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {}
                                });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText loadValue = (EditText) input_loads.findViewById(R.id.load);
                                EditText position = (EditText) input_loads.findViewById(R.id.left_position);

                                if(TextUtils.isEmpty(loadValue.getText())){
                                    loadValue.setError("请输入载荷大小");
                                }
                                if(TextUtils.isEmpty(position.getText())){
                                    position.setError("请输入作用位置");
                                }
                                if((!TextUtils.isEmpty(loadValue.getText()))&&(!TextUtils.isEmpty(position.getText()))) {
                                    Double value = Double.parseDouble(loadValue.getText().toString());
                                    Double Position = Double.parseDouble(position.getText().toString());

                                    Load load = new Load(beam);
                                    load.setType("弯矩");
                                    if(!load.setValue(value)){
                                        loadValue.setError("请输入合适的载荷大小");
                                    }
                                    if(!load.setPosition(Position,Position)){
                                        position.setError("请输入合适的作用位置");
                                    }
                                    if(load.setValue(value) && load.setPosition(Position,Position)){
                                        beam.addLoads(load);
                                        refresh(wrap_draw_beam,beamInfo,solverButton, drawButton);
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });
                    }
                });
                edit_load.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        final View loads_list = inflater.inflate(R.layout.loads_list, null);
                        TextView load_list_info = (TextView) loads_list.findViewById(R.id.load_list_info);

                        if(!beam.getLoads().isEmpty()){
                            load_list_info.setVisibility(View.GONE);
                        }

                        LoadAdapter adapter = new LoadAdapter(MainActivity.this, R.layout.load_item, beam.getLoads(), wrap_draw_beam, beamInfo, solverButton, load_list_info, drawButton);
                        ListView load_list_view = (ListView)  loads_list.findViewById(R.id.loads_list);
                        load_list_view.setAdapter(adapter);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("载荷列表")
                                .setView(loads_list);
                        builder.create().show();
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.select_load_type)
                        .setView(select_loads_type_view);
                if(beam.getState(0)){
                    builder.create().show();
                } else {
                    Toast.makeText(MainActivity.this, "请先输入梁的长度", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //每进行一次梁参数的设定，都调用此方法刷新屏幕上的展示信息
    public void refresh(final LinearLayout wrap_draw_beam, TextView beamInfo, Button solverButton, Button drawButton) {
        DrawBeam drawBeam = (DrawBeam) wrap_draw_beam.getTag();
        drawBeam.postInvalidate();

        beamInfo.setText(beam.info());
        beamInfo.setMovementMethod(new ScrollingMovementMethod());

        if(beam.check()){
            solverButton.setText("求解");
            solverButton.setBackgroundColor(0xffd50000);
            solverButton.setEnabled(true);
        } else {
            solverButton.setText("无法求解");
            solverButton.setBackgroundColor(0xff4db6ac);
            solverButton.setEnabled(false);
        }
        if(!beam.checkAll()) {
            drawButton.setText("无法绘图");
            drawButton.setBackgroundColor(0xff4db6ac);
            drawButton.setEnabled(false);
        }
    }
}
