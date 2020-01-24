package llnl.gnem.core.util;

import java.io.Serializable;

/**
 * Created by: dodge1
 * Date: Feb 1, 2005
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class Pair implements Serializable
{

	protected Object first = null;
	protected Object second = null;
	/**
	 *  Pair constructor comment.
	 */
	public Pair()
	{
	}
	/**
	 *  This method was created in VisualAge.
	 *
	 * @param  first java.lang.Object
	 * @param  second java.lang.Object
	 */
	public Pair(Object first, Object second)
	{
		this.first = first;
		this.second = second;
	}
	/**
	 *  This method was created in VisualAge.
	 *
	 * @param  newValue java.lang.Object
	 */
	public void setFirst(Object newValue)
	{
		this.first = newValue;
	}
	/**
	 *  This method was created in VisualAge.
	 *
	 * @param  newValue java.lang.Object
	 */
	public void setSecond(Object newValue)
	{
		this.second = newValue;
	}
	/**
	 *  This method was created in VisualAge.
	 *
	 * @return  java.lang.Object
	 */
	public Object getFirst()
	{
		return first;
	}
	/**
	 *  This method was created in VisualAge.
	 *
	 * @return  java.lang.Object
	 */
	public Object getSecond()
	{
		return second;
	}
	/**
	 *  This method was created in VisualAge.
	 *
	 * @return  java.lang.String
	 */
	public String toString()
	{
		return "{" + getFirst() + "," + getSecond() + "}";
	}
}
