package Core;

public class ConditionType<Val>{
	private String condition;
	private String ampersant;
	private Val conditionValue;
	private Integer typeVal;
	/**
	 * 
	 * @param conditionString 	- ������ ������� � ������������� ���������
	 * @param conditionValue	- ���� �������� ����
	 * @param typeVal			- ��� �������� 1:int 2:string
	 * @param ampersant			- ��������� ����� ��������
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
