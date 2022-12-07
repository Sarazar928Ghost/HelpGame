package org.starloco.locos.fight.ia.type;

//import java.util.ArrayList;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
//import org.starloco.locos.fight.spells.SpellEffect;
//Based on IA 65 Dopeul Cra for Dopeul Cra
public class IA102 extends AbstractNeedSpell
{
  private byte attack=0;
  private byte move=0;
  private byte isEnFace=0;
  private byte movedFar=0;

  public IA102(Fight fight, Fighter fighter, byte count)
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
	      Fighter C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,maxPo+1);//po max+ 1;
	      Fighter A=Function.getInstance().getNearestAminoinvocnbrcasemax(this.fight,this.fighter,0,maxPo+1+3);// pomax +1;
	      // D�tecte allier � PO max +1 +3 cases (Sur officiel se d�place de 3 cases pour booster son invocateur)
	      if(C!=null&&C.isHide())
	        C=null;
	      //** edit C==null replace with !PathFinding.checkLoS(fight.getMap(), fighter.getCell().getId(), C.getCell().getId(), C)
	      if(this.fighter.getCurPa(this.fight)>0&&!action&&A!=null)
	      {
	    	  if(Function.getInstance().checkIfBuffAvailable(this.fight,this.fighter,this.fighter,this.buffs)&&!PathFinding.isCACwithEnnemy(this.fighter,ennemy)) // V�rification de la disponibilit� sur soit-m�me
				{
			    	Function.getInstance().moveautourIfPossible(this.fight,this.fighter,A); // se place en face
			    	move++;
				}
	        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,A,this.buffs))
	        {
	          time=1000;
	          action=true;
	        }
	      }
	      if(this.fighter.getCurPm(this.fight)>0&&C==null&&this.attack==0)
	      {
	        //int value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,ennemy);
	    	// On essaie d'aller en face pour fl�che ralentissante
	        int value=Function.getInstance().moveenfaceIfPossible(this.fight,this.fighter,ennemy,8); //maxPo+1
	        if(value!=0)
	        {
	          time=value;
	          action=true;
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
	    	 // int celltf=fighter.getCell().getId();
	    	  //int celltc=C.getCell().getId();
	    	  //int idCible = C.getId();
	    	  // Ici on cherchera a se mettre en ligne avant de taper
	    	  // Si on est devant, il fait le tour
	          if(!PathFinding.checkLoS(fight.getMap(), fighter.getCell().getId(), ennemy.getCell().getId(), ennemy))
	        	{
	        	  Function.getInstance().moveautourIfPossible(this.fight,this.fighter,A);
	        	}
	          // On �vite de se d�placer inutilement en ligne si on y est d�j�
	          if(isEnFace==0)
	          {
	        	  value=Function.getInstance().moveenfaceIfPossible(this.fight,this.fighter,ennemy,8); //maxPo+1
	        	  isEnFace++;
	          }
	    	  if(PathFinding.checkLoS(fight.getMap(), fighter.getCell().getId(), C.getCell().getId(), C))
	    	  {
	    		 value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
	    	  }
	    	  else {
	    		  value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,ennemy);
	    		  move++;}
	        if(value!=-1)
	        {
	          time=value;
	          action=true;
	          this.attack++;
	        } else if(this.fighter.getCurPm(this.fight)>0&&this.attack==0&&move==0)
	        {
	          //value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,ennemy);
	        	// On essaie d'aller en face
	          value=Function.getInstance().moveenfaceIfPossible(this.fight,this.fighter,ennemy,8); //maxPo+1
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
	    	if(movedFar==0)
	    	{
	    		int value=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
	    		movedFar++; // �vite qu'il s'�loigne plusieurs fois de suite
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