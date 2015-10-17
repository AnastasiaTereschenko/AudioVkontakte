package com.example.anastasiyaverenich.vkrecipes.attachments;

import android.util.Log;

import com.example.anastasiyaverenich.vkrecipes.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ImagesLayoutManager
{
  private static float calculateMultiThumbsHeight(List<Float> paramList, float paramFloat1, float paramFloat2)
  {
    return (paramFloat1 - paramFloat2 * (-1 + paramList.size())) / sum(paramList);
  }

  private static int oi(char paramChar)
  {
    switch (paramChar)
    {
    case 'w':
    default:
      return 0;
    case 'n':
      return 1;
    case 'q':
    }
    return 2;
  }

  public static void processThumbs(int paramInt1, int paramInt2, ArrayList<ThumbAttachment> paramArrayList)
  {
    ArrayList localArrayList1 = new ArrayList();
    Iterator localIterator1 = paramArrayList.iterator();
    localArrayList1.addAll(paramArrayList);
    if (localArrayList1.size() == 0)
      return;
    String str1 = "";
    int[] arrayOfInt = new int[3];
    ArrayList localArrayList2 = new ArrayList();
    int i = localArrayList1.size();
    int j = 0;
    Iterator localIterator2 = localArrayList1.iterator();
    while (localIterator2.hasNext())
    {
      float f30 = ((ThumbAttachment)localIterator2.next()).getRatio();
      if (f30 == -1.0F)
        j = 1;
      char c;
      if (f30 > 1.2D) {
        c = 'w';
      }
      else if (f30 < 0.8D) {
        c = 'n';
      } else {
        c = 'q';
      }
      str1 = str1 + c;
      localArrayList2.add(Float.valueOf(f30));
    }
    if (j != 0)
    {
      Log.e("vk", "BAD!");
      Iterator localIterator7 = localArrayList1.iterator();
      while (localIterator7.hasNext())
        ((ThumbAttachment)localIterator7.next()).setViewSize(CommonUtils.scale(135.0F), CommonUtils.scale(100.0F), false, false);
      ((ThumbAttachment)localArrayList1.get(-1 + localArrayList1.size())).setPaddingAfter(true);
      return;
    }
    Iterator localIterator3 = localArrayList1.iterator();
    while (localIterator3.hasNext()) {
      ((ThumbAttachment) localIterator3.next()).setPaddingAfter(false);
    }
    ((ThumbAttachment)localArrayList1.get(-1 + localArrayList1.size())).setPaddingAfter(true);
    float f1 = 0;
    float f2 = CommonUtils.scale(2.0F);
    float f3 = CommonUtils.scale(2.0F);;
    float f4 = 1.0f;
    float f5 = 1.0f;
    if (!localArrayList2.isEmpty())
    {
      f1 = sum(localArrayList2) / localArrayList2.size();
      if (paramInt1 <= 0){
        f4 = 320.0F;
        f5 = 210.0F;
      } else {
        f4 = paramInt1;
        f5 = paramInt2;
      }
    }
    float f6;
    float f29;

      f6 = f4 / f5;
    if (i == 1){
      float f28 = CommonUtils.scale(((ThumbAttachment)localArrayList1.get(0)).getWidth('x'));
      f29 = Math.min(f4, f28);
      if (((Float)localArrayList2.get(0)).floatValue() <= 0.5D){
        ((ThumbAttachment)localArrayList1.get(0)).setViewSize(f29, 2.0F * f29, true, false);
        return;
      } else{
        ((ThumbAttachment)localArrayList1.get(0)).setViewSize(f29, f29 / ((Float)localArrayList2.get(0)).floatValue(), true, false);
        return;
      }
    }
    else if (i == 2)
    {
      if ((str1.equals("ww")) && (f1 > 1.4D * f6) && (((Float)localArrayList2.get(1)).floatValue() - ((Float)localArrayList2.get(0)).floatValue() < 0.2D))
      {
        float f26 = f4;
        float f27 = Math.min(f26 / ((Float)localArrayList2.get(0)).floatValue(), Math.min(f26 / ((Float)localArrayList2.get(1)).floatValue(), (f5 - f3) / 2.0F));
        ((ThumbAttachment)localArrayList1.get(0)).setViewSize(f26, f27, true, false);
        ((ThumbAttachment)localArrayList1.get(1)).setViewSize(f26, f27, false, false);
        return;
      }
      if ((str1.equals("ww")) || (str1.equals("qq")))
      {
        float f20 = (f4 - f2) / 2.0F;
        float f21 = Math.min(f20 / ((Float)localArrayList2.get(0)).floatValue(), Math.min(f20 / ((Float)localArrayList2.get(1)).floatValue(), f5));
        ((ThumbAttachment)localArrayList1.get(0)).setViewSize(f20, f21, false, false);
        ((ThumbAttachment)localArrayList1.get(1)).setViewSize(f20, f21, false, false);
        return;
      }
      float f22 = (f4 - f2) / ((Float)localArrayList2.get(1)).floatValue() / (1.0F / ((Float)localArrayList2.get(0)).floatValue() + 1.0F / ((Float)localArrayList2.get(1)).floatValue());
      float f23 = f4 - f22 - f2;
      float f24 = Math.min(f22 / ((Float)localArrayList2.get(0)).floatValue(), f23 / ((Float)localArrayList2.get(1)).floatValue());
      float f25 = Math.min(f5, f24);
      ((ThumbAttachment)localArrayList1.get(0)).setViewSize(f22, f25, false, false);
      ((ThumbAttachment)localArrayList1.get(1)).setViewSize(f23, f25, false, false);
      return;
    }
    if (i == 3)
    {
      if (str1.equals("www"))
      {
        float f16 = f4;
        float f17 = Math.min(f16 / ((Float)localArrayList2.get(0)).floatValue(), 0.66F * (f5 - f3));
        ((ThumbAttachment)localArrayList1.get(0)).setViewSize(f16, f17, true, false);
        float f18 = (f4 - f2) / 2.0F;
        float f19 = Math.min(f5 - f17 - f3, Math.min(f18 / ((Float)localArrayList2.get(1)).floatValue(), f18 / ((Float)localArrayList2.get(2)).floatValue()));
        ((ThumbAttachment)localArrayList1.get(1)).setViewSize(f18, f19, false, false);
        ((ThumbAttachment)localArrayList1.get(2)).setViewSize(f18, f19, false, false);
        return;
      }
      int i26 = (int)f5;
      int i27 = (int)Math.min(i26 * ((Float)localArrayList2.get(0)).floatValue(), 0.75D * (f4 - f2));
      ((ThumbAttachment)localArrayList1.get(0)).setViewSize(i27, i26, false, false);
      float f13 = ((Float)localArrayList2.get(1)).floatValue() * (f5 - f3) / (((Float)localArrayList2.get(2)).floatValue() + ((Float)localArrayList2.get(1)).floatValue());
      float f14 = f5 - f13 - f3;
      float f15 = Math.min(f4 - i27 - f2, Math.min(f13 * ((Float)localArrayList2.get(2)).floatValue(), f14 * ((Float)localArrayList2.get(1)).floatValue()));
      ((ThumbAttachment)localArrayList1.get(1)).setViewSize(f15, f14, false, true);
      ((ThumbAttachment)localArrayList1.get(2)).setViewSize(f15, f13, false, true);
      return;
    }
    if (i == 4)
    {
      if (str1.equals("wwww"))
      {
        int i19 = (int)f4;
        int i20 = (int)Math.min(i19 / ((Float)localArrayList2.get(0)).floatValue(), 0.66D * (f5 - f3));
        ((ThumbAttachment)localArrayList1.get(0)).setViewSize(i19, i20, true, false);
        int i21 = (int)((f4 - 2.0F * f2) / (((Float)localArrayList2.get(1)).floatValue() + ((Float)localArrayList2.get(2)).floatValue() + ((Float)localArrayList2.get(3)).floatValue()));
        int i22 = (int)(i21 * ((Float)localArrayList2.get(1)).floatValue());
        int i23 = (int)(i21 * ((Float)localArrayList2.get(2)).floatValue());
        int i24 = (int)(i21 * ((Float)localArrayList2.get(3)).floatValue());
        int i25 = (int)Math.min(f5 - i20 - f3, i21);
        ((ThumbAttachment)localArrayList1.get(1)).setViewSize(i22, i25, false, false);
        ((ThumbAttachment)localArrayList1.get(2)).setViewSize(i23, i25, false, false);
        ((ThumbAttachment)localArrayList1.get(3)).setViewSize(i24, i25, false, false);
        return;
      }
      int i12 = (int)f5;
      int i13 = (int)Math.min(i12 * ((Float)localArrayList2.get(0)).floatValue(), 0.66D * (f4 - f2));
      ((ThumbAttachment)localArrayList1.get(0)).setViewSize(i13, i12, false, false);
      int i14 = (int)((f5 - 2.0F * f3) / (1.0F / ((Float)localArrayList2.get(1)).floatValue() + 1.0F / ((Float)localArrayList2.get(2)).floatValue() + 1.0F / ((Float)localArrayList2.get(3)).floatValue()));
      int i15 = (int)(i14 / ((Float)localArrayList2.get(1)).floatValue());
      int i16 = (int)(i14 / ((Float)localArrayList2.get(2)).floatValue());
      int i17 = (int)(f3 + i14 / ((Float)localArrayList2.get(3)).floatValue());
      int i18 = (int)Math.min(f4 - i13 - f2, i14);
      ((ThumbAttachment)localArrayList1.get(1)).setViewSize(i18, i15, false, true);
      ((ThumbAttachment)localArrayList1.get(2)).setViewSize(i18, i16, false, true);
      ((ThumbAttachment)localArrayList1.get(3)).setViewSize(i18, i17, false, true);
      return;
    }
    ArrayList localArrayList3 = new ArrayList();
    if (f1 > 1.1D)
    {
      Iterator localIterator6 = localArrayList2.iterator();
      while (localIterator6.hasNext())
        localArrayList3.add(Float.valueOf(Math.max(1.0F, ((Float)localIterator6.next()).floatValue())));
    } else {
      Iterator localIterator4 = localArrayList2.iterator();
      while (localIterator4.hasNext())
        localArrayList3.add(Float.valueOf(Math.min(1.0F, ((Float)localIterator4.next()).floatValue())));
    }
    HashMap localHashMap = new HashMap();
    String str2 = i + "";
    float[] arrayOfFloat1 = new float[1];
    arrayOfFloat1[0] = calculateMultiThumbsHeight(localArrayList3, f4, f2);
    localHashMap.put(str2, arrayOfFloat1);
    for (int k = 1; k <= i - 1; k++)
    {
      String str5 = k + "," + (i - k);
      float[] arrayOfFloat5 = new float[2];
      arrayOfFloat5[0] = calculateMultiThumbsHeight(localArrayList3.subList(0, k), f4, f2);
      arrayOfFloat5[1] = calculateMultiThumbsHeight(localArrayList3.subList(k, localArrayList3.size()), f4, f2);
      localHashMap.put(str5, arrayOfFloat5);
    }
    for (int m = 1; m <= i - 2; m++)
      for (int i10 = 1; ; i10++)
      {
        int i11 = -1 + (i - m);
        if (i10 > i11)
          break;
        String str4 = m + "," + i10 + "," + (i - m - i10);
        float[] arrayOfFloat4 = new float[3];
        arrayOfFloat4[0] = calculateMultiThumbsHeight(localArrayList3.subList(0, m), f4, f2);
        arrayOfFloat4[1] = calculateMultiThumbsHeight(localArrayList3.subList(m, m + i10), f4, f2);
        arrayOfFloat4[2] = calculateMultiThumbsHeight(localArrayList3.subList(m + i10, localArrayList3.size()), f4, f2);
        localHashMap.put(str4, arrayOfFloat4);
      }
    String localObject = null;
    float f7 = 0.0F;
    Iterator localIterator5 = localHashMap.keySet().iterator();
    while (localIterator5.hasNext())
    {
      String str3 = (String)localIterator5.next();
      float[] arrayOfFloat3 = (float[])localHashMap.get(str3);
      float f11 = f3 * (-1 + arrayOfFloat3.length);
      int i8 = arrayOfFloat3.length;
      for (int i9 = 0; i9 < i8; i9++)
        f11 += arrayOfFloat3[i9];
      float f12 = Math.abs(f11 - f5);
      if (str3.indexOf(',') != -1)
      {
        String[] arrayOfString2 = str3.split(",");
        if ((Integer.parseInt(arrayOfString2[0]) > Integer.parseInt(arrayOfString2[1]))
                || ((arrayOfString2.length > 2)
                && (Integer.parseInt(arrayOfString2[1]) > Integer.parseInt(arrayOfString2[2]))))
          f12 = (float)(1.1D * f12);
      }
      if ((localObject != null) && (f12 >= f7))
        continue;
      localObject = str3;
      f7 = f12;
    }
    ArrayList localArrayList4 = (ArrayList)localArrayList1.clone();
    ArrayList localArrayList5 = (ArrayList)localArrayList3.clone();
    String[] arrayOfString1 = localObject.split(",");
    float[] arrayOfFloat2 = (float[])localHashMap.get(localObject);
    int size = (-1 + arrayOfString1.length);
    int n = 0;
    for (int i1 = 0; i1<=size; i1++)
    {
      int i3 = Integer.parseInt(arrayOfString1[i1]);
      ArrayList localArrayList6 = new ArrayList();
      for (int i4 = 0; i4 < i3; i4++)
        localArrayList6.add(localArrayList4.remove(0));
      float f8 = arrayOfFloat2[n];
      n++;
      if ( localArrayList6.size() == 0)
        continue;
      for(int j1=0; j1<localArrayList6.size(); j1++){
        ThumbAttachment localThumbAttachment = (ThumbAttachment)localArrayList6.get(j1);
        float f9 = (int)(f8 * ((Float)localArrayList5.remove(0)).floatValue());
        float f10 = (int)f8;
        localThumbAttachment.setViewSize(f9, f10, true, false);
      }
    }
  }

  private static float sum(List<Float> paramList)
  {
    float f = 0.0F;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
      f += ((Float)localIterator.next()).floatValue();
    return f;
  }
}