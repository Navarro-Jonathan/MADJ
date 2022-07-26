import { useContext, useState } from "react";
import { Link } from 'react-router-dom';
import {
    IconButton, Typography, Stack, AppBar,
    Drawer, Box, List, ListItem, ListItemButton,
    ListItemText, Divider, Toolbar, Collapse
} from "@mui/material";
import { Menu, ShoppingCart, ExpandLess, ExpandMore } from '@mui/icons-material';
import ShoppingCartContext from "../contexts/ShoppingCartContext";



/**
 * Component that displays the website menu bar with product types and shopping cart button
 *
 * @returns {JSX.Element}
 * @constructor
 */
const MenuBar = () => {
    /*Initialize menuOpen to false and func to set state*/
    const [menuOpen, setMenuOpen] = useState(false);
    const [openSubMenu, setOpenSubMenu] = useState(false);
    const { openCart, items } = useContext(ShoppingCartContext);

    /**
     * Sets the menu drawer open or closed
     *
     * @param isOpen {boolean}
     */
    const handleDrawer = (isOpen) => {
        setMenuOpen(isOpen);
    }

    /**
     * Toggles the subMenu in menu drawer
     */
    const toggleSubMenu = () => {
        setOpenSubMenu((!openSubMenu));
    }

    /**
     * List of clothing types and their links in the drop-down menu.
     */
    const menuItems = [
        { link: "catalog", text: 'All' },
        { link: "catalog/tops", text: 'Tops' },
        { link: "catalog/outerwear", text: 'Outerwear'},
        { link: "catalog/shorts", text: 'Shorts'},
        { link: "catalog/pants", text: 'Pants'}
    ];

    return (
        <div>
            <AppBar position="static" color="default">  {/*AppBar has shadows*/}
                <Toolbar>
                    <Stack
                        flex="1"
                        direction="row"
                        justifyContent="space-between"
                        alignItems="center">
                        <IconButton size="large" edge="start" onClick={() => handleDrawer(true)}> {/*When clicked, open=true*/}
                            <Menu sx={{ color: "#3D5B59" }}/>
                        </IconButton>
                        <Link to={"/"} style={{ textDecoration: 'inherit', color: 'inherit' }}>
                            <Typography variant="overline" fontSize={30} sx={{ color: "Black" }}>
                                Sprint
                            </Typography>
                        </Link>
                        <IconButton  size="small" sx={{ textDecoration: 'inherit', color: 'inherit' }} onClick={openCart}>
                            <ShoppingCart sx={{ color: "#3D5B59" }}/>
                            <div>{items.length}</div>
                        </IconButton>

                    </Stack>
                </Toolbar>
            </AppBar>
            <Drawer
                anchor='left'
                open={menuOpen}
                onClose={() => handleDrawer(false)}
            >
                <Box
                    role="presentation"
                    sx={{ width: 250}}
                >
                    <List component="nav" disablePadding>
                        <ListItemButton onClick={toggleSubMenu} sx={{ margin: "10px", padding: "10px"}}>
                            <ListItemText primary={"Clothing"}/>
                            {openSubMenu ? <ExpandLess/> : <ExpandMore/>}
                        </ListItemButton>
                        <Collapse in={openSubMenu} unmountOnExit> {/*Show children when subMenuOpen = true*/}
                            <Divider/>
                            <List component="div">
                                {menuItems.map( ( curr, index ) => (
                                    <ListItem component={Link} to={curr.link} key={curr.text} sx={{ padding: "0", margin:"0", textDecoration: 'inherit', color: 'inherit' }}>
                                        <ListItemButton onClick={() => handleDrawer(false)} sx={{ padding: "10px", pl: 4 }}>
                                            <ListItemText primary={curr.text} />
                                        </ListItemButton>
                                    </ListItem>
                                ))}
                            </List>
                            <Divider/>
                        </Collapse>
                        <ListItem component={Link} to={"/catalog/shoes"} sx={{ padding: 0, margin: 0, marginTop: "8px", textDecoration: 'inherit', color: 'inherit' }}>
                            <ListItemButton onClick={() => handleDrawer(false)} sx={{ pl: 4 }}>
                                <ListItemText primary={"Shoes"}/>
                            </ListItemButton>
                        </ListItem>
                        <ListItem component={Link} to={"/catalog/accessories"} sx={{ padding: 0, margin: 0, marginTop: "8px", textDecoration: 'inherit', color: 'inherit' }}>
                            <ListItemButton onClick={() => handleDrawer(false)} sx={{ pl: 4 }}>
                                <ListItemText primary={"Accessories"}/>
                            </ListItemButton>
                        </ListItem>
                    </List>
                </Box>
            </Drawer>
        </div>
    );
}

export default MenuBar;