import { Card, CardContent, CardMedia, Button, Typography, Stack } from '@mui/material';

/**
 * Component to organize product data in a catalog format
 *
 * @returns {JSX.Element}
 * @constructor
 * @param {string} title The text above description
 * @param {string} desc The text body of the card
 * @param {string} img A url to an image
 * @param {float} price
 * @param onAddToCart Function to update if clicked
 */
const ProductCard = ({title, desc, img, price, onAddToCart}) => {

    return (
        <Card>
            <CardMedia component="img"
                       image={img}
                       alt={title}
            />
            <CardContent>
                <Typography gutterBottom variant="h5" component="div">
                    {title}
                </Typography>
                <Typography varaint="body2" color="text.secondary">
                    {desc}
                </Typography>
                <Stack direction="row" alignItems="center" justifyContent="space-between" paddingTop="20px">
                    <Typography fontWeight="bold" color="text.secondary">${price}</Typography>
                    <Button sx={{ color: 'inherit' }} onClick={onAddToCart}>Add to Cart</Button>

                </Stack>
            </CardContent>
        </Card>
    );
}

export default ProductCard;