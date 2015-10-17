package com.example.anastasiyaverenich.vkrecipes.attachments;

public abstract interface ThumbAttachment
{
  public abstract int getHeight();

  public abstract float getRatio();

  public abstract String getThumbURL();

  public abstract int getWidth();

  public abstract int getWidth(char paramChar);

  public abstract void setPaddingAfter(boolean paramBoolean);

  public abstract void setViewSize(float width, float height, boolean breakAfter, boolean floating);
}
