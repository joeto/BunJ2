package to.joe.bungee;

import java.util.ArrayList;

public class CIAdminList extends ArrayList<String> {
    private static final long serialVersionUID = -1769902768878278209L;

    @Override
    public boolean add(String admin) {
        return super.add(admin.toLowerCase());
    }

    @Override
    public boolean contains(Object admin) {
        return (admin instanceof String) ? super.contains(((String) admin).toLowerCase()) : false;
    }

}
