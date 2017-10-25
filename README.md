# RollView
---

***example***

**xml**

      <com.x.leo.rollview.RollView
                android:layout_width="match_parent"
                android:id = "@+id/rv_me"
                android:layout_height="160dp"
                app:circleColor="@color/white"
                app:circleFilledRadius="@dimen/dp02"
                app:circleRadius="@dimen/dp03"
                app:circleStroke="@dimen/dp01"
                app:circleMargin="@dimen/dp03"
                app:circleToBottom="@dimen/dp10"
                ></com.x.leo.rollview.RollView>
**java**

      mHeaderRollerAdapter = new RollViewAdapter(getContext(),mDatas);
            mHeaderRollerAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int data) {
                    Intent intent = new Intent(getContext(), ActivityDetailAcivity.class);
                    intent.putExtra(FieldParams.ACTIVITY_DETAIL_IMAGE_URL, dataBanner.get(data).getUrl());
                    startActivity(intent);
                }
            });
      mHeaderRoller.setAdapter(mHeaderRollerAdapter);



