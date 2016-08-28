package hust.beamforce;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

public class LoadAdapter extends ArrayAdapter<Load> {
    private Context context;
    private int resourceId;
    LinearLayout wrap_draw_beam;
    TextView beamInfo;
    Button solverButton;
    TextView load_list_info;
    Button drawButton;

    public LoadAdapter(Context context, int resourceId, List<Load> loads, LinearLayout wrap_draw_beam, TextView beamInfo, Button solverButton, TextView load_list_info, Button drawButton) {
        super(context,resourceId,loads);
        this.context =context;
        this.resourceId = resourceId;
        this.wrap_draw_beam = wrap_draw_beam;
        this.beamInfo = beamInfo;
        this.solverButton = solverButton;
        this.load_list_info = load_list_info;
        this.drawButton = drawButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Load load = getItem(position);
        View view;
        ViewHolder holder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            holder = new ViewHolder();
            holder.load_info = (TextView) view.findViewById(R.id.load_info);
            holder.edit_item = (Button) view.findViewById(R.id.edit_item);
            holder.remove_item = (Button) view.findViewById(R.id.remove_item);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.load_info.setText(load.info());
        holder.edit_item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final View input_loads = LayoutInflater.from(getContext()).inflate(R.layout.input_loads, null);
                final EditText loadValue = (EditText) input_loads.findViewById(R.id.load);
                final EditText left_position = (EditText) input_loads.findViewById(R.id.left_position);
                final EditText right_position = (EditText) input_loads.findViewById(R.id.right_position);
                final EditText position = (EditText) input_loads.findViewById(R.id.left_position);

                if(load.getType().equals("均布力")){
                    loadValue.setText(load.getValue().toString());
                    left_position.setText(load.getLeftPosition().toString());
                    right_position.setText(load.getRightPosition().toString());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("请输入均布力")
                            .setView(input_loads)
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

                                Load updateLoad = new Load(MainActivity.beam);
                                updateLoad.setType("均布力");
                                if(!updateLoad.setValue(value)){
                                    loadValue.setError("请输入合适的载荷大小");
                                }
                                if(!updateLoad.setPosition(leftPosition,rightPosition)){
                                    left_position.setError("请输入合适的作用位置");
                                    right_position.setError("请输入合适的作用位置");
                                }
                                if(updateLoad.setValue(value) && updateLoad.setPosition(leftPosition,rightPosition)){
                                    MainActivity.beam.removeLoads(load);
                                    MainActivity.beam.addLoads(updateLoad);
                                    LoadAdapter.this.notifyDataSetChanged();
                                    refresh(wrap_draw_beam,beamInfo,solverButton,drawButton);
                                    dialog.dismiss();
                                }
                            }
                        }
                    });
                }
                if(load.getType().equals("集中力")){
                    TableRow hide_row = (TableRow) input_loads.findViewById(R.id.hide_row);
                    hide_row.setVisibility(View.GONE);
                    loadValue.setText(load.getValue().toString());
                    position.setText(load.getLeftPosition().toString());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("请输入集中力")
                            .setView(input_loads)
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
                            if(TextUtils.isEmpty(loadValue.getText())){
                                loadValue.setError("请输入载荷大小");
                            }
                            if(TextUtils.isEmpty(position.getText())){
                                position.setError("请输入作用位置");
                            }
                            if((!TextUtils.isEmpty(loadValue.getText()))&&(!TextUtils.isEmpty(position.getText()))) {
                                Double value = Double.parseDouble(loadValue.getText().toString());
                                Double Position = Double.parseDouble(position.getText().toString());

                                Load updateLoad = new Load(MainActivity.beam);
                                updateLoad.setType("集中力");
                                if(!updateLoad.setValue(value)){
                                    loadValue.setError("请输入合适的载荷大小");
                                }
                                if(!updateLoad.setPosition(Position,Position)){
                                    position.setError("请输入合适的作用位置");
                                }
                                if(updateLoad.setValue(value) && updateLoad.setPosition(Position,Position)){
                                    MainActivity.beam.removeLoads(load);
                                    MainActivity.beam.addLoads(updateLoad);
                                    LoadAdapter.this.notifyDataSetChanged();
                                    refresh(wrap_draw_beam,beamInfo,solverButton,drawButton);
                                    dialog.dismiss();
                                }
                            }
                        }
                    });
                }
                if(load.getType().equals("弯矩")){
                    TableRow hide_row = (TableRow) input_loads.findViewById(R.id.hide_row);
                    hide_row.setVisibility(View.GONE);
                    loadValue.setText(load.getValue().toString());
                    position.setText(load.getLeftPosition().toString());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("请输入弯矩")
                            .setView(input_loads)
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
                            if(TextUtils.isEmpty(loadValue.getText())){
                                loadValue.setError("请输入载荷大小");
                            }
                            if(TextUtils.isEmpty(position.getText())){
                                position.setError("请输入作用位置");
                            }
                            if((!TextUtils.isEmpty(loadValue.getText()))&&(!TextUtils.isEmpty(position.getText()))) {
                                Double value = Double.parseDouble(loadValue.getText().toString());
                                Double Position = Double.parseDouble(position.getText().toString());

                                Load updateLoad = new Load(MainActivity.beam);
                                updateLoad.setType("弯矩");
                                if(!updateLoad.setValue(value)){
                                    loadValue.setError("请输入合适的载荷大小");
                                }
                                if(!updateLoad.setPosition(Position,Position)){
                                    position.setError("请输入合适的作用位置");
                                }
                                if(updateLoad.setValue(value) && updateLoad.setPosition(Position,Position)){
                                    MainActivity.beam.removeLoads(load);
                                    MainActivity.beam.addLoads(updateLoad);
                                    LoadAdapter.this.notifyDataSetChanged();
                                    refresh(wrap_draw_beam,beamInfo,solverButton,drawButton);
                                    dialog.dismiss();
                                }
                            }
                        }
                    });
                }
            }
        });
        holder.remove_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("是否删除此载荷？")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.beam.removeLoads(load);
                                if(MainActivity.beam.getLoads().isEmpty()){
                                    load_list_info.setVisibility(View.VISIBLE);
                                }
                                LoadAdapter.this.notifyDataSetChanged();
                                refresh(wrap_draw_beam,beamInfo,solverButton,drawButton);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                builder.create().show();
            }
        });
        return view;
    }

    class ViewHolder {
        TextView load_info;
        Button edit_item,remove_item;
    }

    public void refresh(final LinearLayout wrap_draw_beam, TextView beamInfo, Button solverButton, Button drawButton) {
        DrawBeam drawBeam = (DrawBeam) wrap_draw_beam.getTag();
        drawBeam.postInvalidate();

        beamInfo.setText(MainActivity.beam.info());
        beamInfo.setMovementMethod(new ScrollingMovementMethod());
        if(MainActivity.beam.check()){
            solverButton.setText("求解");
            solverButton.setBackgroundColor(0xffd50000);
            solverButton.setEnabled(true);
        } else {
            solverButton.setText("无法求解，请输入合适参数");
            solverButton.setBackgroundColor(0xff4db6ac);
            solverButton.setEnabled(false);
        }
        if(!MainActivity.beam.checkAll()) {
            drawButton.setText("无法绘图");
            drawButton.setBackgroundColor(0xff4db6ac);
            drawButton.setEnabled(false);
        }
    }
}
