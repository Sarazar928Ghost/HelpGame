package org.starloco.locos.fight.ia.type;

//import java.util.ArrayList;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
//import org.starloco.locos.fight.spells.SpellEffect;
//Based on IA 65 Dopeul Cra
public class IA101 extends AbstractNeedSpell
{
  private byte attack=0;
  private byte move=0;
  private byte hasMovedFar=0;

  public IA101(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
	  if(!this.stop&&this.fighter.canPlay()&&this.count>0)
	    {
	      int time=100,maxPo=1;
	      boolean action=false;

	      for(Spell.SortStats spellStats : this.highests)
	        if(spellStats.getMaxPO()>maxPo)
	          maxPo=spellStats.getMaxPO();
	      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
	      Fighter C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,maxPo+1);//maxPo+1;
	      Fighter A=Function.getInstance().getNearestAminoinvocnbrcasemax(this.fight,this.fighter,0,4);// pomax +1; 3 PO +1 car trop relou sinon avec d�placement puis boost amplification
	      if(C!=null&&C.isHide())
	        C=null;
	      //** edit C==null replace with !PathFinding.checkLoS(fight.getMap(), fighter.getCell().getId(), C.getCell().getId(), C)
	      if(this.fighter.getCurPa(this.fight)>0&&!action&&A!=null&&!PathFinding.isCACwithEnnemy(this.fighter,ennemy))//&&!PathFinding.isCACwithEnnemy(this.fighter,ennemy)
	      {
	    	// Id�alement ne devrait pas chercher � d�tacler pour boost si est au cac
	    	//Function.getInstance().movediagIfPossible(this.fight,this.fighter,A);
	    	Function.getInstance().moveautourIfPossible(this.fight,this.fighter,A); // se place en face
	    	move++;
	        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,A,this.buffs))
	        {
	          time=1000;
	          action=true;
	        }
	      }
	      if(this.fighter.getCurPm(this.fight)>0&&C==null&&this.attack==0)
	      {
	    	int value=0;
	        value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,ennemy);
	    	//value=Function.getInstance().moveenfaceIfPossible(this.fight,this.fighter,ennemy,maxPo+1); //maxPo+1
	        if(value!=0)
	        {
	          time=value;
	          //action=true;
	          C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,maxPo+1);
	        }
	      }
	      if(this.fighter.getCurPa(this.fight)>0&&!action)
	      {
	        if(Function.getInstance().invocIfPossible(this.fight,this.fighter,this.invocations))
	        {
	          time=600;
	          action=true;
	        }
	      }
	      if(this.fighter.getCurPa(this.fight)>0&&!action)
	      {
	        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,this.fighter,this.buffs))
	        {
	          time=400;
	          action=true;
	        }
	      }
	      if(this.fighter.getCurPa(this.fight)>0&&C!=null&&!action)
	      {
	    	 //insert condition on Los
	    	  int value=-1;
	    	  //int mapt=(int) fight.getMap().getId();
	    	  //int celltf=fighter.getCell().getId();
	    	  //int celltc=C.getCell().getId();
	    	  //int idCible = C.getId();
	    	  if(PathFinding.checkLoS(fight.getMap(), fighter.getCell().getId(), C.getCell().getId(), C))
	    	  {
	    		 for(int i=0; i<3; i++) // Attaque 3 fois de suite
	    		 {
	    			 value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
	    		 }
	    	  }
	    	  else {
	    		  value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,ennemy);
	    		  //value=Function.getInstance().moveenfaceIfPossible(this.fight,this.fighter,ennemy,8); //maxPo+1
	    		  move++;}
	        if(value!=-1)
	        {
	          time=value;
	          action=true;
	          this.attack++;
	        } else if(this.fighter.getCurPm(this.fight)>0&&this.attack==0&&move==0)
	        {
	          value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,ennemy);
	          //value=Function.getInstance().moveenfaceIfPossible(this.fight,this.fighter,ennemy,8); //maxPo+1
	          if(value!=0)
	          {
	            time=value;
	            action=true;
	            Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,maxPo+1);
	          }
	        }
	      }
	      if(this.fighter.getCurPm(this.fight)>0&&!action&&this.attack>0)
	      {
	    	if(hasMovedFar==0)
	    	{
		        int value=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
		        hasMovedFar++;
		        if(value!=0)
		          time=value;
	    	}
	      }

	      if(this.fighter.getCurPa(this.fight)==0&&this.fighter.getCurPm(this.fight)==0)
	        this.stop=true;
	      addNext(this::decrementCount,time);
	    } else
	    {
	      this.stop=true;
	    }
	  }
}