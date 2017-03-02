package Core;

public class ConditionType<Val>{
	private String condition;
	private String ampersant;
	private Val conditionValue;
	private Integer typeVal;
	/**
	 * 
	 * @param conditionString 	- Строка условия с экранируемыми символами
	 * @param conditionValue	- Само значение поля
	 * @param typeVal			- Тип значения 1:int 2:string
	 * @param ampersant			- Амперсант перед условием
	 */
	protected void init(String conditionString, Val conditionValue, int typeVal, String ampersant){
		this.condition = new String(conditionString);
		if(typeVal == 2)
			this.conditionValue = (Val) new String((String) conditionValue);
		else
			this.conditionValue = conditionValue;
		this.typeVal = typeVal;
		this.ampersant = new String(ampersant);
	}
	public ConditionType(String conditionString, Val conditionValue, int typeVal){
		init(conditionString, conditionValue, typeVal,"AND");
	}

	public ConditionType(String conditionString, Val conditionValue, int typeVal, String ampersant){
		init(conditionString, conditionValue, typeVal,ampersant);
	}

	public String getConditionString(){
		return condition;
	}
	public String getAmpersant(){
		return ampersant;
	}
	public Val getConditionValue(){
		return conditionValue;
	}
	public Integer getTypeVal(){
		return typeVal;
	}
}
