//package dev.latvian.mods.sluice.recipe;
//
//import dev.latvian.mods.sluice.block.MeshType;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.item.crafting.Recipe;
//import net.minecraft.world.item.crafting.RecipeSerializer;
//import net.minecraft.world.item.crafting.RecipeType;
//import net.minecraft.world.level.Level;
//
//import java.util.HashSet;
//import java.util.List;
//
///**
// * @author LatvianModder
// */
//public class IngredientPropertiesRecipe implements Recipe<NoInventory> {
//	private final ResourceLocation id;
//	public String group;
//	public int noItemWeight;
//	public int time;
//	public Ingredient ingredient;
//	public HashSet<MeshType> meshes;
//
//	public List<ItemWithWeight> items;
//	public int totalWeight;
//
//	public IngredientPropertiesRecipe(ResourceLocation i, String g) {
//		id = i;
//		group = g;
//		noItemWeight = 0;
//		time = 80;
//		ingredient = Ingredient.EMPTY;
//		meshes = new HashSet<>();
//	}
//
//	@Override
//	public boolean matches(NoInventory inv, Level world) {
//		return true;
//	}
//
//	@Override
//	public ItemStack assemble(NoInventory inv) {
//		return ItemStack.EMPTY;
//	}
//
//	@Override
//	public boolean canCraftInDimensions(int width, int height) {
//		return true;
//	}
//
//	@Override
//	public ItemStack getResultItem() {
//		return ItemStack.EMPTY;
//	}
//
//	@Override
//	public ResourceLocation getId() {
//		return id;
//	}
//
//	@Override
//	public String getGroup() {
//		return group;
//	}
//
//	@Override
//	public RecipeSerializer<?> getSerializer() {
//		return SluiceModRecipeSerializers.INGREDIENT_PROPERTIES.get();
//	}
//
//	@Override
//	public RecipeType<?> getType() {
//		return SluiceModRecipeSerializers.INGREDIENT_PROPERTIES_TYPE;
//	}
//}