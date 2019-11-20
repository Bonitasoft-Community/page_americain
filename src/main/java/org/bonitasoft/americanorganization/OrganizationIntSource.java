package org.bonitasoft.americanorganization;

/**
 * describe a new Source to load organization. The source will be call first on
 * a initInput, then by multiple getNextItem until there are no more item to
 * return. Then a endInput is called.
 */
public interface OrganizationIntSource {

    /**
     * to read Value, the initLoading is perform first
     * 
     * @throws Exception
     */
    public void initInput(OrganizationLog organizationLog) throws Exception;

    /**
     * get the next Item form the source, or null if there are no more item
     * available in the source An Item is different object : ItelmUser,
     * ItemGroup, ItemRole, ItemMemberShip, ItemProfile. Each Item
     * 
     * @param identityAPI
     * @return a Item, or null is the input is finish.
     */
    public Item getNextItem(OrganizationLog organizationLog) throws Exception;

    /**
     * end the input of the data
     */
    public void endInput(OrganizationLog organizationLog) throws Exception;

}
